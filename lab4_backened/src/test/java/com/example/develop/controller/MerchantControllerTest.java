package com.example.develop.controller;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.service.MerchantFilter;
import com.example.develop.service.MerchantService;
import com.example.develop.service.MerchantSorting;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MerchantController.class)
@ExtendWith(MockitoExtension.class)
public class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MerchantService merchantService;

    @MockitoBean
    private MerchantFilter merchantFilter;

    @MockitoBean
    private MerchantSorting merchantSorting;

    @InjectMocks
    private MerchantController merchantController;





    // 对API端口"/api/merchants/filterAndSort"进行测试


    // 1. 正常请求
    @Test
    void testNormalRequest() throws Exception {
        List<MerchantDTO> mockMerchants = List.of(
                new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(200), true, "8:00-22:00", null, null),
                new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                        3.9, new BigDecimal(150), false, "8:00-22:00", null, null)
        );

        when(merchantFilter.filterMerchants(any())).thenReturn(mockMerchants);
        when(merchantSorting.sortMerchant(any(), any())).thenReturn(mockMerchants);

        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("上海", "素食"), 2.0, 5.0,
                new BigDecimal(10), new BigDecimal(500), new BigDecimal(15), new BigDecimal(400),
                false, true, false, false
        );

        mockMvc.perform(post("/api/merchants/filterAndSort")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("商家1"))
                .andExpect(jsonPath("$[1].name").value("商家2"));
    }


    // 2. 缺失参数时使用默认参数
    @Test
    void testLackParamsRequest() throws Exception {
        List<MerchantDTO> mockMerchants = List.of(
                new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(200), true, "8:00-22:00", null, null),
                new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                        3.9, new BigDecimal(150), false, "8:00-22:00", null, null)
        );

        when(merchantFilter.filterMerchants(any())).thenReturn(mockMerchants);
        when(merchantSorting.sortMerchant(any(), any())).thenReturn(mockMerchants);

        mockMvc.perform(post("/api/merchants/filterAndSort")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) // 发送一个空的请求体
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("商家1"))
                .andExpect(jsonPath("$[1].name").value("商家2"));
    }


    // 3. 含非法参数时无视非法参数
    @Test
    void testProblemParamsRequest() throws Exception {
        List<MerchantDTO> mockMerchants = List.of(
                new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(200), true, "8:00-22:00", null, null),
                new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                        3.9, new BigDecimal(150), false, "8:00-22:00", null, null)
        );

        when(merchantFilter.filterMerchants(any())).thenReturn(mockMerchants);
        when(merchantSorting.sortMerchant(any(), any())).thenReturn(mockMerchants);

        mockMvc.perform(post("/api/merchants/filterAndSort")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wtfField\": \"wtfValue\"}")) // 发送一个包含非法字段的请求体
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("商家1"))
                .andExpect(jsonPath("$[1].name").value("商家2"));
    }


    // 4. 请求参数不合法时返回正确HTTP状态码
    @Test
    void testInvalidParamsRequest() throws Exception {
        mockMvc.perform(post("/api/merchants/filterAndSort")
                .contentType(MediaType.APPLICATION_JSON)
                .content("123456"))
                .andExpect(status().isBadRequest());
    }


    // 5. 请求类型不合法时返回正确HTTP状态码
    @Test
    void testInvalidRequestType() throws Exception {
        mockMvc.perform(post("/api/merchants/filterAndSort")
                .contentType(MediaType.APPLICATION_XML) // 使用不支持的媒体类型
                .content("<request><param>value</param></request>"))
                .andExpect(status().isUnsupportedMediaType());
    }


    // 6. 请求正确但没有符合要求的商家时返回空列表
    @Test
    void testNoMatchingMerchants() throws Exception {
        when(merchantFilter.filterMerchants(any())).thenReturn(List.of());
        when(merchantSorting.sortMerchant(any(), any())).thenReturn(List.of());

        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("不存在的商家"), 0.0, 0.0,
                new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
                false, false, false, false
        );

        mockMvc.perform(post("/api/merchants/filterAndSort")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
