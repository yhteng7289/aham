package com.pivot.aham.common.enums.app;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public enum CostTypeEnum implements IEnum {
    ART_S(1, "Arts (Singapore)", CostCountriesTypeEnum.Singapore, CostCourseTypeEnum.ARTS, new BigDecimal("135000")),
    ART_A(2, "Arts (Australia)", CostCountriesTypeEnum.Australia, CostCourseTypeEnum.ARTS, new BigDecimal("195000")),
    ART_UK(3, "Arts (United Kingdom)", CostCountriesTypeEnum.UnitedKingdom, CostCourseTypeEnum.ARTS, new BigDecimal("165000")),
    ART_US(4, "Arts (United States)", CostCountriesTypeEnum.UnitedStates, CostCourseTypeEnum.ARTS, new BigDecimal("300000")),
    AL_S(5, "Architecture / Law (Singapore)", CostCountriesTypeEnum.Singapore, CostCourseTypeEnum.AL, new BigDecimal("220000")),
    AL_A(6, "Architecture / Law (Australia)", CostCountriesTypeEnum.Australia, CostCourseTypeEnum.AL, new BigDecimal("300000")),
    AL_UK(7, "Architecture / Law (United Kingdom)", CostCountriesTypeEnum.UnitedKingdom, CostCourseTypeEnum.AL, new BigDecimal("260000")),
    AL_US(8, "Architecture / Law (United States)", CostCountriesTypeEnum.UnitedStates, CostCourseTypeEnum.AL, new BigDecimal("420000")),
    BA_S(9, "Business / Accountancy (Singapore)", CostCountriesTypeEnum.Singapore, CostCourseTypeEnum.BA, new BigDecimal("200000")),
    BA_A(10, "Business / Accountancy (Australia)", CostCountriesTypeEnum.Australia, CostCourseTypeEnum.BA, new BigDecimal("260000")),
    BA_UK(11, "Business / Accountancy (United Kingdom)", CostCountriesTypeEnum.UnitedKingdom, CostCourseTypeEnum.BA, new BigDecimal("260000")),
    BA_US(12, "Business / Accountancy (United States)", CostCountriesTypeEnum.UnitedStates, CostCourseTypeEnum.BA, new BigDecimal("420000")),
    ESC_S(13, "Engineering / Science / Computing (Singapore)", CostCountriesTypeEnum.Singapore, CostCourseTypeEnum.ESC, new BigDecimal("220000")),
    ESC_A(14, "Engineering / Science / Computing (Australia)", CostCountriesTypeEnum.Australia, CostCourseTypeEnum.ESC, new BigDecimal("280000")),
    ESC_UK(15, "Engineering / Science / Computing (United Kingdom)", CostCountriesTypeEnum.UnitedKingdom, CostCourseTypeEnum.ESC, new BigDecimal("300000")),
    ESC_US(16, "Engineering / Science / Computing (United States)", CostCountriesTypeEnum.UnitedStates, CostCourseTypeEnum.ESC, new BigDecimal("420000")),
    MED_S(17, "Medicine (5-year course) (Singapore)", CostCountriesTypeEnum.Singapore, CostCourseTypeEnum.MED, new BigDecimal("270000")),
    MED_A(18, "Medicine (5-year course) (Australia)", CostCountriesTypeEnum.Australia, CostCourseTypeEnum.MED, new BigDecimal("450000")),
    MED_UK(19, "Medicine (5-year course) (United Kingdom)", CostCountriesTypeEnum.UnitedKingdom, CostCourseTypeEnum.MED, new BigDecimal("510000")),
    MED_US(20, "Medicine (5-year course) (United States)", CostCountriesTypeEnum.UnitedStates, CostCourseTypeEnum.MED, new BigDecimal("690000")),;

    private Integer value;
    private String desc;
    private CostCountriesTypeEnum costCountries;
    private CostCourseTypeEnum costCourse;
    private BigDecimal cost;

    CostTypeEnum(int value, String desc, CostCountriesTypeEnum costCountries, CostCourseTypeEnum costCourse, BigDecimal cost) {
        this.value = value;
        this.desc = desc;
        this.costCountries = costCountries;
        this.costCourse = costCourse;
        this.cost = cost;
    }

    public static CostTypeEnum getCostByCourseAndCountry(CostCountriesTypeEnum costCountries, CostCourseTypeEnum costCourse) {
        for (CostTypeEnum costTypeEnum : values()) {
            if (costTypeEnum.getCostCountries() == costCountries && costTypeEnum.getCostCourse() == costCourse) {
                return costTypeEnum;
            }
        }

        throw new IllegalArgumentException("未找到对应cost类别：userType=" + costCountries.getValue() + ", aumLevel=" + costCourse.getValue());
    }

    public static CostTypeEnum forValue(Integer value) {
        for (CostTypeEnum courseTypeEnum : CostTypeEnum.values()) {
            if (Objects.equals(courseTypeEnum.getValue(), value)) {
                return courseTypeEnum;
            }
        }
        return null;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public CostCountriesTypeEnum getCostCountries() {
        return costCountries;
    }

    public void setCostCountries(CostCountriesTypeEnum costCountries) {
        this.costCountries = costCountries;
    }

    public CostCourseTypeEnum getCostCourse() {
        return costCourse;
    }

    public void setCostCourse(CostCourseTypeEnum costCourse) {
        this.costCourse = costCourse;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public Serializable getValue() {
        return value;
    }
}
