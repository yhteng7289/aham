package com.pivot.aham.common.enums.app;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * @author YYYz
 */
public enum CountriesEnum implements IEnum {
    Singapore(0,"Singapore"),
    Australia(38,"Australia"),
    Austria(10,"Austria"),
    Bahamas(68,"Bahamas"),
    Bahrain(60,"Bahrain"),
    Bangladesh(39,"Bangladesh"),
    Belgium(11,"Belgium"),
    Brazil(67,"Brazil"),
    Brunei(6,"Brunei"),
    Bulgaria(12,"Bulgaria"),
    Cambodia(9,"Cambodia"),
    Canada(40,"Canada"),
    Chile(69,"Chile"),
    China(41,"China"),
    Croatia(13,"Croatia"),
    Cyprus(14,"Cyprus"),
    CzechRepublic(15,"Czech Republic"),
    Denmark(16,"Denmark"),
    Estonia(17,"Estonia"),
    Finland(18,"Finland"),
    France(19,"France"),
    Germany(20,"Germany"),
    Greece(21,"Greece"),
    HongKong(42,"Hong Kong"),
    Hungary(22,"Hungary"),
    Iceland(53,"Iceland"),
    India(43,"India"),
    Indonesia(2,"Indonesia"),
    Ireland(23,"Ireland"),
    Israel(61,"Israel"),
    Italy(24,"Italy"),
    Japan(44,"Japan"),
    Kuwait(63,"Kuwait"),
    Laos(8,"Laos"),
    Latvia(25,"Latvia"),
    Liechtenstein(54,"Liechtenstein"),
    Lithuania(26,"Lithuania"),
    Luxembourg(55,"Luxembourg"),
    Macau(46,"Macau"),
    Malaysia(1,"Malaysia"),
    Malta(28,"Malta"),
    Mongolia(47,"Mongolia"),
    Myanmar(7,"Myanmar"),
    Netherlands(29,"Netherlands"),
    NewZealand(48,"New Zealand"),
    Norway(56,"Norway"),
    Oman(64,"Oman"),
    Pakistan(49,"Pakistan"),
    Philippines(4,"Philippines"),
    Poland(30,"Poland"),
    Portugal(31,"Portugal"),
    Qatar(65,"Qatar"),
    Romania(32,"Romania"),
    Russia(58,"Russia"),
    SaudiArabia(66,"Saudi Arabia"),
    Slovakia(33,"Slovakia"),
    Slovenia(34,"Slovenia"),
    SouthAfrica(70,"South Africa"),
    SouthKorea(45,"South Korea"),
    Spain(35,"Spain"),
    SriLanka(50,"Sri Lanka"),
    Sweden(59,"Sweden"),
    Switzerland(57,"Switzerland"),
    Taiwan(51,"Taiwan"),
    Thailand(3,"Thailand"),
    UnitedArabEmirates(62,"United Arab Emirates"),
    UnitedKingdom(37,"United Kingdom"),
    USA(52,"USA"),
    Vietnam(5,"Vietnam");
//    Luxembourg(27,"Luxembourg"),
//    Sweden(36,"Sweden"),

    CountriesEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;
    private String desc;

    @Override
    public Serializable getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
