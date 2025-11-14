package com.example.develop.service;

import com.example.develop.DTO.DishDTO;
import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantImageDTO;
import com.example.develop.entity.Dish;
import com.example.develop.entity.Merchant;
import com.example.develop.entity.MerchantImage;
import com.example.develop.exception.ResourceNotFoundException;
import com.example.develop.repository.MerchantImageRepository;
import com.example.develop.repository.MerchantRepository;
import com.example.develop.utils.PinyinUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


import java.util.List;

import java.util.stream.Collectors;

// 服务实现
@Service
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final MerchantImageRepository imageRepository;

    @Autowired
    public MerchantServiceImpl(MerchantRepository merchantRepository,
                               MerchantImageRepository imageRepository) {
        this.merchantRepository = merchantRepository;
        this.imageRepository = imageRepository;
    }


    @Override
    public MerchantDTO getMerchantById(@NotNull Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商家不存在，ID: " + id));
        //现阶段无论是通过id查找还是直接查找 都不需要返回图片
        return MerchantDTO.fromEntity(merchant, false,true,true);
    }


    @Override
    public List<MerchantImageDTO> getMerchantImagesById(@NotNull Long merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new ResourceNotFoundException("商家不存在，ID: " + merchantId);
        }

        List<MerchantImage> images = imageRepository.findByMerchantId(merchantId);
        return images.stream()
                .map(MerchantImageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public MerchantDTO getMerchantWithImages(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new EntityNotFoundException("商家不存在"));

        // 转换为DTO并携带图片信息
        return MerchantDTO.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .images(merchant.getImages().stream()
                        .map(MerchantImageDTO::fromEntity)
                        .collect(Collectors.toList())) // 修正流操作
                .build();
    }


    @Override
    public List<MerchantDTO> getAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();
        return merchants.stream()
                .map(merchant -> MerchantDTO.fromEntity(merchant, false,false,false))
                .collect(Collectors.toList());
    }


    @Override
    public List<MerchantDTO> searchMerchants(List<String> keywords) {
        List<Merchant> merchants = merchantRepository.findAll(buildMerchantSpecification(keywords));
        // 按相关度排序（Java 端实现）
        merchants.sort((m1, m2) -> {
            int score1 = calculateRelevanceScore(m1, keywords);
            int score2 = calculateRelevanceScore(m2, keywords);
            return Integer.compare(score2, score1); // 降序排列
        });

        return merchants.stream()
                .map(merchant -> MerchantDTO.fromEntity(merchant, false,false, false))
                .collect(Collectors.toList());
    }


    private int calculateRelevanceScore(Merchant merchant, List<String> keywords) {
        int score = 0;

        for (String keyword : keywords) {
            if (keyword == null || keyword.isEmpty()) {
                continue;
            }

            String pinyinKeyword = PinyinUtils.toFuzzyPinyin(keyword);

            // 商家名称匹配
            if (merchant.getName().contains(keyword) ||
                    merchant.getNamePinyin().contains(pinyinKeyword)) {
                score += 5;
            }

            // 菜品名称或类别匹配
            score += getDishMatchScore(merchant.getDishes(), keyword);

            // 地址匹配
            if (merchant.getAddress().contains(keyword)) {
                score += 2;
            }

            // 描述匹配
            String description = merchant.getDescription();
            if (description != null && description.contains(keyword)) {
                score += 1;
            }
        }

        return score;
    }

    private int getDishMatchScore(List<Dish> dishes, String keyword) {
        if (dishes == null || dishes.isEmpty()) {
            return 0;
        }

        for (Dish dish : dishes) {
            if (dish.getName().contains(keyword) || dish.getCategory().contains(keyword)) {
                return 3; // 匹配任意一个菜品即可加分
            }
        }

        return 0;
    }



    /**
     * 构建 Specification，支持多个关键词匹配多个字段
     */
    // 修改后的 buildMerchantSpecification 方法
    private Specification<Merchant> buildMerchantSpecification(List<String> keywords) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Predicate predicate = criteriaBuilder.conjunction();
            if (keywords != null && !keywords.isEmpty()) {
                List<Predicate> keywordPredicates = keywords.stream()
                        .filter(k -> !k.trim().isEmpty())
                        .map(keyword -> {
                            // 转换关键词为拼音
                            String pinyinKeyword = PinyinUtils.toFuzzyPinyin(keyword);
                            String pattern = "%" + keyword + "%";
                            String pinyinPattern = "%" + pinyinKeyword + "%";

                            // 商家名称（中文+拼音）
                            Predicate nameChinese = criteriaBuilder.like(root.get("name"), pattern);
                            Predicate namePinyin = criteriaBuilder.like(root.get("namePinyin"), pinyinPattern);
                            Predicate nameMatch = criteriaBuilder.or(nameChinese, namePinyin);

                            // 地址（中文+拼音）
                            Predicate addressChinese = criteriaBuilder.like(root.get("address"), pattern);
                            Predicate addressPinyin = criteriaBuilder.like(root.get("addressPinyin"), pinyinPattern);
                            Predicate addressMatch = criteriaBuilder.or(addressChinese, addressPinyin);

                            // 菜品匹配（名称+类别，中文+拼音）
                            Join<Merchant, Dish> dishJoin = root.join("dishes", JoinType.LEFT);
                            // 菜品名称
                            Predicate dishNameChinese = criteriaBuilder.like(dishJoin.get("name"), pattern);
                            Predicate dishNamePinyin = criteriaBuilder.like(dishJoin.get("namePinyin"), pinyinPattern);
                            Predicate dishNameMatch = criteriaBuilder.or(dishNameChinese, dishNamePinyin);
                            // 菜品类别
                            Predicate dishCategoryChinese = criteriaBuilder.like(dishJoin.get("category"), pattern);
                            Predicate dishCategoryPinyin = criteriaBuilder.like(dishJoin.get("categoryPinyin"), pinyinPattern);
                            Predicate dishCategoryMatch = criteriaBuilder.or(dishCategoryChinese, dishCategoryPinyin);
                            // 组合菜品条件
                            Predicate dishMatch = criteriaBuilder.or(dishNameMatch, dishCategoryMatch);

                            // 最终组合：任意字段匹配即可
                            return criteriaBuilder.or(
                                    nameMatch,
                                    addressMatch,
                                    dishMatch
                            );
                        })
                        .collect(Collectors.toList());
                // 关键修改：用 AND 连接不同关键词的条件
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.and(keywordPredicates.toArray(new Predicate[0])));
            }
            return predicate;
        };
    }

}
