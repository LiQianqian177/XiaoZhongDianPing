package com.example.develop.config;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.CouponType;
import com.example.develop.entity.CouponStatus;
import com.example.develop.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class CouponDataInitializer implements CommandLineRunner {
    @Autowired
    private CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        if (hasNewUserCoupon()){
            return;
        }
        // 创建新人KFC9折券
        Coupon kfcCoupon = new Coupon.Builder()
                .setName("新人KFC9折券")
                .setDescription("新人KFC9折券：满10元可用、整单9折券、仅限KFC南区店使用、领取后7天内有效")
                .setType(CouponType.DISCOUNT)
                .setAmount(new BigDecimal("90.0")) // 9折
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.now().plusDays(7))
                .setUsageThreshold(new BigDecimal("10.0"))
                .setApplicableStore("KFC南区店")
                .setTotalQuantity(10000)
                .setQuantityPerUser(1)
                .setIsForever(true)
                .setNewUserCoupon(true)
                .build();

        // 创建新人奶茶免单券
        Coupon teaCoupon = new Coupon.Builder()
                .setName("新人奶茶免单券")
                .setDescription("新人奶茶免单券：无门槛免单券、最高抵扣15元、适用品类：奶茶、领取后7天内有效")
                .setType(CouponType.FIXED_AMOUNT)
                .setAmount(new BigDecimal("15.0")) // 最高抵扣15元
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.now().plusDays(7))
                .setUsageThreshold(BigDecimal.ZERO) // 无门槛
                .setApplicableCategory("奶茶")
                .setTotalQuantity(100)
                .setIsForever(true)
                .setQuantityPerUser(1)
                .setNewUserCoupon(true)
                .build();

        // 创建新人100元优惠券
        Coupon bigCoupon = new Coupon.Builder()
                .setName("新人100元优惠券")
                .setDescription("新人100元优惠券：满200减100元券、全品类通用、领取后1天内有效")
                .setType(CouponType.FIXED_AMOUNT)
                .setAmount(new BigDecimal("100.0")) // 满200减100元
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.now().plusDays(1))
                .setUsageThreshold(new BigDecimal("200.0"))
                .setTotalQuantity(1)
                .setIsForever(true)
                .setQuantityPerUser(1)
                .setNewUserCoupon(true)
                .build();
        Coupon secKillCoupon = new Coupon.Builder()
                .setName("0.1元秒杀券")
                .setDescription("满0元可用，优惠后价格0.1元，最高抵扣20元，全品类适用，永久有效")
                .setType(CouponType.REDUCE_TO_FIXED_AMOUNT)
                .setAmount(new BigDecimal("0.1"))
                .setUsageThreshold(BigDecimal.ZERO)
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                .setTotalQuantity(10000)
                .setIsForever(true)
                .setQuantityPerUser(1)
                .setNewUserCoupon(false)
                .build();


        // Amy的5折券
        Coupon halfPriceCoupon = new Coupon.Builder()
                .setName("5折奖励券")
                .setDescription("满0元可用，整单5折优惠，全品类适用，永久有效")
                .setType(CouponType.DISCOUNT)
                .setAmount(new BigDecimal("50.0")) // 百分比折扣用绝对值表示
                .setUsageThreshold(BigDecimal.ZERO)
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                .setTotalQuantity(5000)
                .setQuantityPerUser(2)
                .setNewUserCoupon(false)
                .build();

        // Amy的西餐券
        Coupon westernCoupon = new Coupon.Builder()
                .setName("西餐减150元券")
                .setDescription("满0元立减150元，限西餐品类使用，永久有效")
                .setType(CouponType.FIXED_AMOUNT)
                .setAmount(new BigDecimal("150"))
                .setUsageThreshold(BigDecimal.ZERO)
                .setApplicableCategory("西餐")
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.now().plusDays(14))
                .setTotalQuantity(2000)
                .setIsForever(true)
                .setQuantityPerUser(1)
                .setNewUserCoupon(false)
                .build();

        // Bob的喜茶券
        Coupon heyteaCoupon = new Coupon.Builder()
                .setName("喜茶免单券")
                .setDescription("最高抵扣20元，限喜茶五角场万达店使用，永久有效")
                .setType(CouponType.REDUCE_TO_FIXED_AMOUNT)
                .setAmount(new BigDecimal("20"))
                .setUsageThreshold(BigDecimal.ZERO)
                .setApplicableStore("喜茶奶茶（五角场万达店）")
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                .setTotalQuantity(300)
                .setIsForever(true)
                .setQuantityPerUser(1)
                .setNewUserCoupon(false)
                .build();

        // Bob的6折券
        Coupon sixthDiscountCoupon = new Coupon.Builder()
                .setName("6折奖励券")
                .setDescription("满0元可用，整单6折优惠，全品类适用，永久有效")
                .setType(CouponType.DISCOUNT)
                .setAmount(new BigDecimal("60.0"))
                .setUsageThreshold(BigDecimal.ZERO)
                .setValidFrom(LocalDateTime.now())
                .setValidTo(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                .setTotalQuantity(3000)
                .setIsForever(true)
                .setQuantityPerUser(1)
                .setNewUserCoupon(false)
                .build();

        // 保存优惠券到数据库
        couponRepository.save(kfcCoupon);
        couponRepository.save(teaCoupon);
        couponRepository.save(bigCoupon);
        couponRepository.save(westernCoupon);
        couponRepository.save(heyteaCoupon);
        couponRepository.save(sixthDiscountCoupon);
        couponRepository.save(halfPriceCoupon);
        couponRepository.save(secKillCoupon);
    }
    public boolean hasNewUserCoupon() {
        return couponRepository.existsByIsNewUserCouponTrue();
    }
}