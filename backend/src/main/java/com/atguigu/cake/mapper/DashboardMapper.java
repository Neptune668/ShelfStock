package com.atguigu.cake.mapper;

import com.atguigu.cake.vo.OutboundReasonVO;
import com.atguigu.cake.vo.SaleTrendVO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 看板统计 Mapper：集中看板所需的聚合统计 SQL
 */
public interface DashboardMapper {

    @Select("SELECT IFNULL(SUM(quantity), 0) FROM material_batch")
    BigDecimal sumMaterialQuantity();

    @Select("SELECT COUNT(*) FROM material")
    Long countMaterial();

    @Select("SELECT IFNULL(SUM(quantity), 0) FROM material_inbound WHERE created_at >= #{start} AND created_at < #{end}")
    BigDecimal sumInboundQuantity(@Param("start") Date start, @Param("end") Date end);

    @Select("SELECT IFNULL(SUM(quantity), 0) FROM material_outbound WHERE created_at >= #{start} AND created_at < #{end}")
    BigDecimal sumOutboundQuantity(@Param("start") Date start, @Param("end") Date end);

    @Select("SELECT IFNULL(SUM(quantity), 0) FROM product_batch WHERE status IN ('ON_SALE', 'DISCOUNT')")
    BigDecimal sumOnSaleQuantity();

    @Select("SELECT COUNT(*) FROM product")
    Long countProduct();

    @Select("SELECT COUNT(*) FROM sale_record WHERE created_at >= #{start} AND created_at < #{end}")
    Long countSale(@Param("start") Date start, @Param("end") Date end);

    @Select("SELECT IFNULL(SUM(actual_price * quantity), 0) FROM sale_record WHERE created_at >= #{start} AND created_at < #{end}")
    BigDecimal sumSaleAmount(@Param("start") Date start, @Param("end") Date end);

    @Select("SELECT COUNT(*) FROM material_batch WHERE expiry_date >= #{today} AND expiry_date <= DATE_ADD(#{today}, INTERVAL near_expiry_days DAY)")
    Long countNearExpiryMaterialBatch(@Param("today") Date today);

    @Select("SELECT COUNT(*) FROM product_batch WHERE expiry_date >= #{today} AND expiry_date <= DATE_ADD(#{today}, INTERVAL near_expiry_days DAY)")
    Long countNearExpiryProduct(@Param("today") Date today);

    @Select("SELECT COUNT(*) FROM material_batch WHERE expiry_date < #{today}")
    Long countExpiredMaterialBatch(@Param("today") Date today);

    @Select("SELECT COUNT(*) FROM product_batch WHERE expiry_date < #{today} AND status != 'OFF_SHELF'")
    Long countExpiredProduct(@Param("today") Date today);

    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS date, COUNT(*) AS sale_count, IFNULL(SUM(actual_price * quantity), 0) AS sale_amount "
            + "FROM sale_record WHERE created_at >= #{start} AND created_at < #{end} "
            + "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') ORDER BY date")
    List<SaleTrendVO> listSaleTrend(@Param("start") Date start, @Param("end") Date end);

    @Select("SELECT reason, IFNULL(SUM(quantity), 0) AS quantity FROM material_outbound GROUP BY reason")
    List<OutboundReasonVO> listOutboundReason();
}
