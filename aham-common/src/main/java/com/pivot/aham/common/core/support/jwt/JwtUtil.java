package com.pivot.aham.common.core.support.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import com.pivot.aham.common.core.support.keystore.KeyStoreUtil;
import com.pivot.aham.common.core.util.DateUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月21日
 */
public class JwtUtil {

    public static String createJwtToken() throws Exception {
        PrivateKey privateKey = KeyStoreUtil.getPrivateKey("config/dev/2B71D9F0000100000504.p12", "HFSuCIOh");
        PublicKey publicKey = KeyStoreUtil.getPublicKey("config/dev/2B71D9F0000100000504.p12", "HFSuCIOh");
        Certificate certificate = KeyStoreUtil.getCertificate("config/dev/2B71D9F0000100000504.p12", "HFSuCIOh");
        String thum = KeyStoreUtil.getThumbprint(certificate);

        Date issueDate = DateUtils.now();
        Date expireDate = DateUtils.addSecond(issueDate, 10);

        Map<String, Object> headerClaims = Maps.newHashMap();
        headerClaims.put("x5t", thum);
        headerClaims.put("alg", "RS256");
        headerClaims.put("typ", "JWT");
        headerClaims.put("kid", thum);

        String token = JWT.create()
                .withIssuer("02080951-08f6-4919-9739-3512854a70e0")
                .withAudience("https://sim.logonvalidation.net")
                .withHeader(headerClaims)
                .withClaim("spurl", "https://logonvalidation.net/sim/pivotapp")
                .withClaim("sub", "9081284")
                .withClaim("iss", "02080951-08f6-4919-9739-3512854a70e0")
                .withClaim("aud", "https://sim.logonvalidation.net")
                //        .withClaim("exp", Long.valueOf(expireDate.getTime()).toString())
                .withExpiresAt(expireDate)
                .withNotBefore(null)
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey));

        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) publicKey, null)).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        System.out.println(decodedJWT.getClaims().get("spurl").asString());

        return token;
    }

    public static void main(String[] args) throws Exception {
//        PrivateKey privateKey = KeyStoreUtil.getPrivateKey("/Users/pintec/IdeaProjects/2B71D9F0000100000504.p12","HFSuCIOh");
//        PublicKey publicKey = KeyStoreUtil.getPublicKey("/Users/pintec/IdeaProjects/2B71D9F0000100000504.p12","HFSuCIOh");
//        Certificate certificate = KeyStoreUtil.getCertificate("/Users/pintec/IdeaProjects/2B71D9F0000100000504.p12","HFSuCIOh");
//        String thum = KeyStoreUtil.getThumbprint(certificate);
        //        Map<String, Object> headerClaims = Maps.newLinkedHashMap();
//        headerClaims.put("alg","RS256");
//        headerClaims.put("kid",thum.toUpperCase());
//        headerClaims.put("typ","JWT");
//        headerClaims.put("x5t",thum.toUpperCase());
//
//
//        String[] s = new String[2];
//        s[0]= "https://sim.logonvalidation.net";
//        s[1]= "https://sim.logonvalidation.net";
//
//        PublicKey publicKey = getPublicKey(pubStr);
//        PrivateKey privateKey = getPrivateKey(priStr);
//
//
//
//
//        String token = JWT.create()
//        .withIssuer("02080951-08f6-4919-9739-3512854a70e0")
//        .withAudience("https://sim.logonvalidation.net")
//        .withHeader(headerClaims)
//        .withClaim("spurl", "https://logonvalidation.net/sim/pivotapp")
//        .withClaim("sub", "9081284")
//        .withClaim("iss", "02080951-08f6-4919-9739-3512854a70e0")
//        .withArrayClaim("aud", s)
//        .withExpiresAt(DateUtils.addDateByDay(DateUtils.now(),1))
//        .sign(Algorithm.RSA256((RSAPublicKey) publicKey,(RSAPrivateKey) privateKey));

//        System.out.println(new JwtUtil().createJwtToken());
    }

}
