package com.example.develop.service;

import com.example.develop.entity.Dish;
import com.example.develop.entity.Groupbuy;
import com.example.develop.entity.Merchant;
import com.example.develop.entity.MerchantImage;
import com.example.develop.repository.MerchantRepository;
import com.example.develop.repository.DishRepository;
import com.example.develop.repository.GroupbuyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MerchantDataInitializationService {

    private static final int MAX_MERCHANT_IMAGES = 3;// 修改为实际图片数量（例如10张图）
    private static final int INITIAL_MERCHANT_COUNT = 50;
    private final MerchantRepository merchantRepository;
    private final DishRepository dishRepository;
    private final GroupbuyRepository groupbuyRepository;
    private final Random random = new Random();


    @PostConstruct
    public void initializeData() {
        if(merchantRepository.count() == 0) {
            // 清除现有数据（可选）
            dishRepository.deleteAll();
            merchantRepository.deleteAll();
            groupbuyRepository.deleteAll();

            // 生成50个商家
            List<Merchant> merchants = generateMerchants(50);
            merchantRepository.saveAll(merchants);

            // 添加预设商家和团购套餐
            addPresetMerchantsAndGroupbuys();

            // 为每个商家生成随机数量的菜品
            for (Merchant merchant : merchants) {
                List<Dish> dishes = generateDishesForMerchant(merchant, 5 + random.nextInt(10));
                dishRepository.saveAll(dishes);

                // 为每个商家生成随机数量的团购套餐
                List<Groupbuy> groupbuys = generateGroupbuysForMerchant(merchant, 2 + random.nextInt(5));
                groupbuyRepository.saveAll(groupbuys);
            }
        }
    }

    private void addPresetMerchantsAndGroupbuys() {
        // 新发现（五角场万达店）
        Map<String, String[]> addresses1 = new HashMap<>();
        addresses1.put("杨浦区", new String[]{"五角场环岛路"});
        Merchant merchant1 = createSingleMerchant(new String[]{"新发现中餐（五角场万达店）"}, addresses1, new String[]{"11:00-22:00"});
        merchantRepository.save(merchant1);
        List<Dish> dishes1 = generateDishesForMerchant(merchant1, 5 + random.nextInt(10));
        dishRepository.saveAll(dishes1);
        List<Groupbuy> groupbuys1 = Arrays.asList(
                generateSingleGroupbuy(merchant1, "家庭小聚三人餐", dishes1, BigDecimal.valueOf(199)),
                generateSingleGroupbuy(merchant1, "招牌甄选三人餐", dishes1, BigDecimal.valueOf(209))
        );
        groupbuyRepository.saveAll(groupbuys1);

        // 茶百道（五角场中心店）
        Map<String, String[]> addresses2 = new HashMap<>();
        addresses2.put("杨浦区", new String[]{"淞沪路"});
        Merchant merchant2 = createSingleMerchant(new String[]{"茶百道奶茶（五角场中心店）"}, addresses2, new String[]{"11:00-22:00"});
        merchantRepository.save(merchant2);
        List<Dish> dishes2 = generateDishesForMerchant(merchant2, 5 + random.nextInt(10));
        dishRepository.saveAll(dishes2);
        List<Groupbuy> groupbuys2 = Collections.singletonList(
                generateSingleGroupbuy(merchant2, "葡萄系列3选1", dishes2, BigDecimal.valueOf(11))
        );
        groupbuyRepository.saveAll(groupbuys2);

        // 喜茶（五角场万达店）
        Map<String, String[]> addresses3 = new HashMap<>();
        addresses3.put("杨浦区", new String[]{"国宾路"});
        Merchant merchant3 = createSingleMerchant(new String[]{"喜茶奶茶（五角场万达店）"}, addresses3, new String[]{"11:00-22:00"});
        merchantRepository.save(merchant3);
        List<Dish> dishes3 = generateDishesForMerchant(merchant3, 5 + random.nextInt(10));
        dishRepository.saveAll(dishes3);
        List<Groupbuy> groupbuys3 = Collections.singletonList(
                generateSingleGroupbuy(merchant3, "时令白芭乐2选1", dishes3, BigDecimal.valueOf(19))
        );
        groupbuyRepository.saveAll(groupbuys3);
    }



    private Merchant createSingleMerchant(String[] merchantNames, Map<String, String[]> addresses, String[] businessHours) {
        Merchant merchant = new Merchant();
        merchant.setName(merchantNames[random.nextInt(merchantNames.length)]);
        merchant.setDescription("这是一家非常棒的餐厅，提供地道美味的" + merchant.getName() + "餐饮");


        // 随机选择一个区
        List<String> districts = new ArrayList<>(addresses.keySet());
        String district = districts.get(random.nextInt(districts.size()));

        // 随机选择该区的一条路
        String[] roads = addresses.get(district);
        String road = roads[random.nextInt(roads.length)];

        // 组合成完整地址
        String fullAddress = "上海市" + district + road;
        merchant.setAddress(fullAddress);
        merchant.setPhone("1" + (10000 + random.nextInt(90000)) + "0000");
        merchant.setRating(2.5 + random.nextDouble() * 2.5);
        merchant.setIsOpen(true);
        merchant.setBusinessHours(businessHours[0]);

        // 随机设置价格范围
        BigDecimal minPrice = BigDecimal.valueOf(20 + random.nextInt(30));
        BigDecimal maxPrice = minPrice.add(BigDecimal.valueOf(50 + random.nextInt(100)));
        merchant.setMinPrice(minPrice);
        merchant.setMaxPrice(maxPrice);
        merchant.setAverageCost(minPrice.add(maxPrice).divide(BigDecimal.valueOf(2)));

        // 为商家添加随机图片
        int imageCount = 3 + random.nextInt(3);
        for (int j = 0; j < imageCount; j++) {
            MerchantImage image = MerchantImage.builder()
                    .imageUrl(generateRandomImageUrl())
                    .merchant(merchant)
                    .build();
            merchant.getImages().add(image);
        }
        return merchant;
    }

    private List<Merchant> generateMerchants(int count) {
        List<Merchant> merchants = new ArrayList<>();
        // 更新商户名称列表，增加特色店铺类型
        String[] merchantNames = {
                "重庆火锅店", "川味火锅", "海底捞火锅",
                "coco奶茶", "一点点奶茶", "古茗奶茶",
                "星巴克咖啡", "瑞幸咖啡", "爱和猫协咖啡厅",
                "日式寿司店", "意大利披萨屋", "韩国炸鸡",
                "沙县小吃", "兰州拉面", "东北饺子馆"
        };
        Map<String, String[]> districtRoadMap = new HashMap<>();
        districtRoadMap.put("黄浦区", new String[]{"南京东路", "淮海东路", "西藏中路"});
        districtRoadMap.put("徐汇区", new String[]{"漕溪北路", "衡山路", "淮海中路"});
        districtRoadMap.put("长宁区", new String[]{"延安西路", "天山路", "仙霞路"});
        districtRoadMap.put("静安区", new String[]{"南京西路", "常德路", "万航渡路"});
        districtRoadMap.put("普陀区", new String[]{"中山北路", "长寿路", "曹杨路"});
        districtRoadMap.put("虹口区", new String[]{"四川北路", "海宁路", "欧阳路"});
        districtRoadMap.put("杨浦区", new String[]{"五角场环岛路", "淞沪路", "国宾路"});
        districtRoadMap.put("闵行区", new String[]{"莘庄地铁南广场", "七莘路", "顾戴路"});
        districtRoadMap.put("宝山区", new String[]{"牡丹江路", "友谊路", "同济路"});
        districtRoadMap.put("嘉定区", new String[]{"博乐路", "塔城路", "城中路"});
        districtRoadMap.put("浦东新区", new String[]{"世纪大道", "张杨路", "浦东南路"});
        String[] businessHours = {
                "11:00-22:00", "10:30-23:00", "12:00-21:30", "16:00-01:00"
        };

        for (int i = 0; i < count; i++) {
            Merchant merchant = createSingleMerchant(merchantNames,districtRoadMap, businessHours);
            merchants.add(merchant);
        }
        return merchants;
    }
    private String generateRandomImageUrl() {
        // 生成 1-12 的随机整数（包含1和12）
        int imageNumber = 1 + random.nextInt(30); // nextInt(12) 生成 0-11，+1 后为 1-12
        return "uploads/merchant-images/" + imageNumber + ".jpg";
    }

    private List<Dish> generateDishesForMerchant(Merchant merchant, int count) {
        List<Dish> dishes = new ArrayList<>();
        String merchantName = merchant.getName();
        // 根据店铺类型选择菜品模板
        Map<String, String[]> dishTemplates = getDishTemplateByMerchantName(merchantName);
        for (int i = 0; i < count; i++) {
            Dish dish = new Dish();
            // 根据模板生成菜品
            String category = getRandomKey(dishTemplates);
            String[] dishOptions = dishTemplates.get(category);
            String dishName = dishOptions[random.nextInt(dishOptions.length)];
            dish.setName(dishName);
            dish.setCategory(category);
            dish.setDescription(generateDishDescription(dishName, merchantName));
            dish.setPrice(calculateDishPrice(merchantName, category));
            dish.setMerchant(merchant);
            dish.setIsAvailable(true); // 简化逻辑，默认可用
            dishes.add(dish);
        }
        return dishes;
    }
    // 根据店铺名称返回菜品模板
    private Map<String, String[]> getDishTemplateByMerchantName(String tpye) {
        Map<String, String[]> template = new LinkedHashMap<>();
        if (tpye.contains("火锅")) {
            template.put("锅底", new String[]{"麻辣锅底", "番茄锅底", "菌汤锅底"});
            template.put("肉类", new String[]{"肥牛卷", "羊肉卷", "毛肚", "黄喉"});
            template.put("海鲜", new String[]{"虾滑", "鲍鱼", "扇贝"});
            template.put("蔬菜", new String[]{"娃娃菜", "金针菇", "土豆片"});
        } else if (tpye.contains("奶茶")) {
            template.put("经典奶茶", new String[]{"珍珠奶茶", "布丁奶茶", "红豆奶茶"});
            template.put("水果茶", new String[]{"芒果冰沙", "葡萄柚绿茶", "芝士草莓"});
            template.put("芝士系列", new String[]{"芝士芒芒", "芝士葡萄", "芝士桃桃"});
            template.put("小料", new String[]{"珍珠", "椰果", "仙草"});
        } else if (tpye.contains("咖啡")) {
            template.put("意式咖啡", new String[]{"美式咖啡", "拿铁", "卡布奇诺"});
            template.put("特调饮品", new String[]{"抹茶星冰乐", "焦糖玛奇朵", "摩卡可可"});
            template.put("甜点", new String[]{"提拉米苏", "芝士蛋糕", "马卡龙"});
            template.put("轻食", new String[]{"三明治", "贝果", "沙拉"});
        } else {
            // 通用模板（保留原有逻辑）
            template.put("主食", new String[]{"米饭", "面条", "饺子", "馒头"});
            template.put("热菜", new String[]{"宫保鸡丁", "回锅肉", "麻婆豆腐", "鱼香肉丝"});
            template.put("汤类", new String[]{"番茄蛋汤", "紫菜汤", "玉米排骨汤"});
            template.put("甜点", new String[]{"冰淇淋", "蛋糕", "布丁"});
        }
        return template;
    }
    // 生成符合店铺类型的菜品描述
    private String generateDishDescription(String dishName, String merchantName) {
        if (merchantName.contains("火锅")) {
            return "火锅必点：" + dishName + "，新鲜食材，涮煮更美味";
        } else if (merchantName.contains("奶茶")) {
            return "人气饮品：" + dishName + "，精选原料，现做现卖";
        } else if (merchantName.contains("咖啡")) {
            return "现磨咖啡：" + dishName + "，采用阿拉比卡咖啡豆";
        }
        return "招牌菜品：" + dishName + "，地道风味，值得品尝";
    }
    // 根据店铺类型设定价格区间
    private BigDecimal calculateDishPrice(String merchantName, String category) {
        double basePrice = 0;
        if (merchantName.contains("火锅")) {
            basePrice = switch (category) {
                case "锅底" -> 48 + random.nextInt(30);
                case "肉类" -> 28 + random.nextInt(20);
                default -> 15 + random.nextInt(15);
            };
        } else if (merchantName.contains("奶茶")) {
            basePrice = switch (category) {
                case "芝士系列" -> 18 + random.nextInt(10);
                default -> 12 + random.nextInt(8);
            };
        } else if (merchantName.contains("咖啡")) {
            basePrice = switch (category) {
                case "意式咖啡" -> 25 + random.nextInt(15);
                case "甜点" -> 28 + random.nextInt(20);
                default -> 18 + random.nextInt(12);
            };
        } else {
            basePrice = 10 + random.nextInt(40);
        }
        return BigDecimal.valueOf(basePrice).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateGroupbuyTitle(Merchant merchant, List<Dish> selectedDishes) {
        String merchantName = merchant.getName();
        int dishCount = selectedDishes.size();
        String mealCount = dishCount + "人餐";
        if (merchantName.contains("火锅")) {
            return "火锅超值" + mealCount+ ":" + generateGroupbuyContent(selectedDishes) + " 套餐";
        } else if (merchantName.contains("奶茶")) {
            return "奶茶精选" + mealCount + ":" + generateGroupbuyContent(selectedDishes) + " 精选";
        } else if (merchantName.contains("咖啡")) {
            return "咖啡美食" + mealCount + ":" + generateGroupbuyContent(selectedDishes) + "任选";
        }
        return "超值" + mealCount + "：" + generateGroupbuyContent(selectedDishes) + " 组合";
    }

    private Groupbuy generateSingleGroupbuy(Merchant merchant, String title,List<Dish> merchantDishes, BigDecimal price) {
        // 随机选择菜品数量（至少1道菜，最多为商家已有菜品总数）
        int dishCount = 1 + random.nextInt(Math.min(3, merchantDishes.size()));

        // 随机选择菜品
        Collections.shuffle(merchantDishes);
        List<Dish> selectedDishes = merchantDishes.subList(0, dishCount);

        if (price == null) {
            price = calculateGroupbuyPrice(selectedDishes);
        }
        String content = generateGroupbuyContent(selectedDishes);

        //根据商家类型和菜品生成更有吸引力的标题
        if(title == null) {
            title = generateGroupbuyTitle(merchant, selectedDishes);
        }

        Groupbuy groupbuy = Groupbuy.builder()
                .title(title)
                .price(price)
                .description("超值套餐：" + title + "，仅需 " + price + " 元！")
                .content(content)
                .sales(0)
                .merchant(merchant)
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusDays(30))
                .isAvailable(true)
                .build();

        return groupbuy;
    }



    private List<Groupbuy> generateGroupbuysForMerchant(Merchant merchant, int count) {
        List<Groupbuy> groupbuys = new ArrayList<>();
        List<Dish> merchantDishes = dishRepository.findByMerchantId(merchant.getId());

        // 如果商家没有菜品，则不生成套餐
        if (merchantDishes.isEmpty()) return groupbuys;

        for (int i = 0; i < count; i++) {
            Groupbuy groupbuy = generateSingleGroupbuy(merchant,null,merchantDishes, null);
            groupbuys.add(groupbuy);
        }
        return groupbuys;
    }
    //随机选择商家已有菜品的
    private BigDecimal calculateGroupbuyPrice(List<Dish> selectedDishes) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Dish dish : selectedDishes) {
            totalPrice = totalPrice.add(dish.getPrice());
        }

        // 套餐价格给予一定折扣（例如 8 折）
        return totalPrice.multiply(BigDecimal.valueOf(0.8)).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateGroupbuyContent(List<Dish> selectedDishes) {
        StringBuilder contentBuilder = new StringBuilder();

        for (int i = 0; i < selectedDishes.size(); i++) {
            contentBuilder.append(selectedDishes.get(i).getName());
            if (i < selectedDishes.size() - 1) {
                contentBuilder.append("，");
            }
        }

        return contentBuilder.toString();
    }

    // 辅助方法：随机获取Map的Key
    private static <K, V> K getRandomKey(Map<K, V> map) {
        List<K> keys = new ArrayList<>(map.keySet());
        return keys.get(new Random().nextInt(keys.size()));
    }
}