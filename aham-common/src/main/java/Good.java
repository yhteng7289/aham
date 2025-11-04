
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivot.aham.common.core.support.security.SecuredGCMUsage;
import static com.pivot.aham.common.core.support.security.SecuredGCMUsage.TAG_BIT_LENGTH;
import com.pivot.aham.common.core.support.security.SecuredRSAUsage;
import com.pivot.aham.common.core.util.SecurityKeyUtils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author HP
 */
public class Good {

    public static void main(String args[]) throws IOException, Exception {
//        Sequence sequence = new Sequence();
//        System.out.println(sequence.nextId());
        try {
//        File f = new File("C:/projects/certificate/03F63418@B5AE9A7F.FC5A735C");
//        FileInputStream fis = new FileInputStream(f);
//        byte[] keyBytes;
//        try (DataInputStream dis = new DataInputStream(fis)) {
//            keyBytes = new byte[(int) f.length()];
//            dis.readFully(keyBytes);
//        }
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey pk = kf.generatePrivate(spec);
//        System.out.println(pk);

            String publicKeyCert = "-----BEGIN CERTIFICATE-----\n"
                    + "MIID3jCCAsYCCQDxPxpblLPGeDANBgkqhkiG9w0BAQsFADCBsDELMAkGA1UEBhMC\n"
                    + "U0cxEjAQBgNVBAgMCVNpbmdhcG9yZTESMBAGA1UEBwwJU2luZ2Fwb3JlMR8wHQYD\n"
                    + "VQQKDBZQSVZPVCBGaW50ZWNoIFB0ZS4gTHRkMRIwEAYDVQQLDAlNYXJrZXRpbmcx\n"
                    + "GDAWBgNVBAMMD3NxdWlycmVsc2F2ZS5haTEqMCgGCSqGSIb3DQEJARYbam9obm55\n"
                    + "Lm9uZ0BwaXZvdGZpbnRlY2guY29tMB4XDTE5MDIyMTAzMzM1OFoXDTIwMDIyMTAz\n"
                    + "MzM1OFowgbAxCzAJBgNVBAYTAlNHMRIwEAYDVQQIDAlTaW5nYXBvcmUxEjAQBgNV\n"
                    + "BAcMCVNpbmdhcG9yZTEfMB0GA1UECgwWUElWT1QgRmludGVjaCBQdGUuIEx0ZDES\n"
                    + "MBAGA1UECwwJTWFya2V0aW5nMRgwFgYDVQQDDA9zcXVpcnJlbHNhdmUuYWkxKjAo\n"
                    + "BgkqhkiG9w0BCQEWG2pvaG5ueS5vbmdAcGl2b3RmaW50ZWNoLmNvbTCCASIwDQYJ\n"
                    + "KoZIhvcNAQEBBQADggEPADCCAQoCggEBALnxvkzkiRkewCBFYuQkyTi1W4OvwQTo\n"
                    + "MyoeTnKNuGDaQ7pIq+TroNDyQGt2QLBo5eCGMWc1B8NIdYdbGKu0Q+o6rjQTR+va\n"
                    + "PLPBOikp3bRBk5KNl3wNgt5U/jYb5pipgEttsrAYS1rUL8u+U7PhFDU2rgA4Tzsx\n"
                    + "HozVwwLBcd2RKD3S/W4meurd7osB6tAVrd6yquViqYjo6bunTFqA2uyAL2M62WEH\n"
                    + "BY6OZGQBRA98+ZImoRFXMLAflBAB6quKOa3ekoKcntafmHJy9s3rN/dgnwq0dFCy\n"
                    + "BE1tQlcd/Mrjv4xC7nPpJHHHj36PNJ7A/H15/Og9/a312A8mvCVxnvECAwEAATAN\n"
                    + "BgkqhkiG9w0BAQsFAAOCAQEAEUgTMYininpEDPsUXccGfXlXi1NGoyUwFIz/xa4P\n"
                    + "06d8+GvcEMYH0nTfIIjdj0o6tucCo+FkRNvxBM7TgCkhYPNnpP3PEgNZLRKoKkR7\n"
                    + "PcRerMCmtg4Teozlh8oGW4Us/G6IwcOucQ6dsI+lvfJjpZwiVfGGOTG00DrljyqI\n"
                    + "BktEGDrH3sBstd8x09JGrNK91oIRLCT3VSx1a5aCVZE8RdmAZ50E4giyjkS9RARe\n"
                    + "KR/dwtrgfpEmW57h6Wz4xLzIl6HF6QCq2qzc3DqdqCIbKixcbRG5a2+CRvKL6w3H\n"
                    + "StbF8jW2LdUDMtzJCvGj2CrIrOdOIVXo1d+0jxRJugSa9Q==\n"
                    + "-----END CERTIFICATE-----";

            String privateKeyCert = "-----BEGIN PRIVATE KEY-----\n"
                    + "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQC17BJVI5P82SQ2\n"
                    + "16vD2dwXEqe5OiGbRRpu46m+parrr9bzKs/YmnCwU9HKEdPjD+oTjUer4c/cdPoU\n"
                    + "xn+6yfPw2L14T2HWEhEpjkSbYYKtjqnugnGNstmXimga17PYcp4jp2o9lnoWNHRf\n"
                    + "H++0LRm7Spc+7OzIeU2XvtLEIPRfdR8HDBvHN+4mLzFUoAsiI1Ah7bEqwGrN2bP6\n"
                    + "UfjPjHXtTtG2IoaL/WyoIKx9JBVzWBYiY+q/X4tQi8MY5bCfyFaERxf4GbTii6KB\n"
                    + "BJDQ+Sx/iLD7jmamJwvqDZeHT2ocS+v9XTVMnoXCjh3MYl7wy6kk4qEqRls8LlHs\n"
                    + "PUzt1HWdfdB6IN8fu0Flel7fOn1x9s2s/vb/XbsqNXsBdKzs7jR21tUU9O/3vlhA\n"
                    + "e9JhWhvH30jz77oXo4+l0BRsbjGrNmFRaiYFIo9e0CtUAnSZ4DRuAzHugD4DsMvf\n"
                    + "bkAW8+ClGOw3zXRn0khQLuDXEr9kUrA+ag0Mt1yUYIdRkK58lxVjUd8w7V/QF2ig\n"
                    + "EB9i7mDpZT7TL5Lth8/qB9g61/rJSjavb3myRX/3GzNttVpt4Qqi1/0IaHopgyF6\n"
                    + "cbQu2Q76e92Wcy562+A2rngtjn1nU+R++0i+6C9gGbT4QKq2uowiP95fEL7PdilG\n"
                    + "AszqA9vF+WfM7P2z+r0e4W709KTU2QIDAQABAoICAGVz1XVtdeYYIj4DYGLEaBzl\n"
                    + "5mtBo+Q3UVpt1Afh08PEOBc71NVyq01qMSCDeKq0AVIOA94y+/xxr9VOLYU5grTZ\n"
                    + "6i/h9SmwwalvEYulgX7QhKviSvk+uGHuacguarlWkiJIBtN5y3P+pLc31DGixSrR\n"
                    + "mi8ZaaMTItZxMJD339AsPLVtZ0BeTI023EfZSNws3U9OgvLYg7dwpRIADMWSC2Hg\n"
                    + "BsuPzYDcP3AzaH4RVXURtqZPGmbHHaY5iv+jeDBjRPxXuWI+08VTIbjZlvkzPEGT\n"
                    + "NT/UViJqvBY2pj5rZz2IiX6uMD8eWHH7V1rXsSKQMS/EwyQUQGT7X+xi8zfeuAe/\n"
                    + "dgl4Kv0/LC97wjZLvy+mdrsrbdsqfsHa3pHwl8CbBE3I9gODnf6YAuvoCNJfIvFe\n"
                    + "A3b3MjNadxP5V0RAso8WklMQntG5tjxXVDVccR2ixJmY3nSeStYwjt9fMQUSI6HW\n"
                    + "ANR+OIUHoHIfgG5FRxpck42f5HbtrgvrXh6c5qDZaSDs2jKT7BKzNgdV9iHwcb/J\n"
                    + "oHR9U1EMC7lxsDakL3dH01IZZQyaaDECHohsvOoK4ah/KwzSJpwcOg3axNG6rWRh\n"
                    + "HqPK0Vt9Kzo5Uhj7NPPcBNYqcHE9f8J6YMNVv84pp0aAMnAngF0bhEEKPwGZAgsf\n"
                    + "bxW/XSVTO/dva+lwuvgBAoIBAQDgNJ5nOeC7cKJYVWNYo7EYvVyDC8DYV8CaiCqi\n"
                    + "o0f+4wfO9il7yrTxMzmBl7lcPz44mfSF6HIzATYBienC0DYrt0IRvayURD5vp4GT\n"
                    + "leAFSp3L2KjZXzCO4vwIfQNeuRwNB6Op4NrRvgqBh353Pe8uKkWKNrITSi2ny/yJ\n"
                    + "pU2xoaa4Iyngl65Kh4sZU1qZgqjIAU5UUq+UjWExNwcYncyPF3hc5A3K3CuaADGe\n"
                    + "PvDjf1wxRQ4g42BudnldgUrOJzM524pFwTBNyNvvb72UkBx+4vIcucgfye+m+FEq\n"
                    + "q5u7RvA+2I64TRSvqGVwMJ/oky5mZOJhvujkOkfRrH2M8HGZAoIBAQDPuG4agBWa\n"
                    + "BCgU/y/bFoAiaS0GtnCy4EIPgBJrpfKxeeLR8AkTp2q2ftp22Q2Ha58wsbVmOXaB\n"
                    + "RB22FjJeybam2r5hgA+JjwcQ2yYslxj6FOcDNPp5dQaMtOpcCXvX42VXEwr/bRZf\n"
                    + "grkciMvOJwvTUFvA6zxWpsXqrGmn0nraSX9Pt5rNPxsb6apiQ3lIJnP5udHdhZHe\n"
                    + "UzkIi8tLNs1xDWHZ2qorptMxhTKKj1nbee/uDjeL5pe/0IFzjZwDEQcCVH99Nse4\n"
                    + "tyxRD2z/SvZ9wNduIdYZq1gdF2QMpQAW9qHFczGGssFGwpNPSHX7iq99wiFiy4z4\n"
                    + "L8a0D7vgYAVBAoIBAQCtGTLq846EaLOOi25GMys5tIlGS765yRZUuKhiJLojGdkf\n"
                    + "eoIQpzIHvsq5ORDNHa7GOGbHM+1nX5n4mie2VtZb7gK+e7hQSFgEEElETsREkZU+\n"
                    + "mqnSGWH+D1USoKIHcdcsYCeFsqp+oMCvvJJxMYoGFHDYAt59chnJoP36r3tKl0mG\n"
                    + "hO+CDNlthTRpeydJuczhjdCR3bzdbkIJoziBUuNp3m75GMFlS5MGNPwZ9vI4Uk41\n"
                    + "Y+FClwoOfr246Wd9vloYY11f+YV3qInVVGoD1zeI52KRtwXBNUEnojPYQ+HaNyyo\n"
                    + "+L1HoYtFCzg14/SxtWBvgZgeayyXeD+WrBvKwqCRAoIBAQCYfD1nestgythZwZNx\n"
                    + "86yQlkEVgmxtA6+7DTt4htQIcP+POfSP1wDBg5IAWQpTdS/hYTNXHave7RAakWTc\n"
                    + "rJtHFaFYldfSFYsMVrRJW4pcW2fStNuCuePa+xxoABZHv4ivSS1zoLMh3ibhyZJD\n"
                    + "fepyb7VLg+RBgCAeS/8r0ZmaEQZCkPg037SY4wnPJNUtn/zXQA/N3LXeTZ26plTQ\n"
                    + "nCRob3h7osf1T3wOCEe368/9DCCT0x/3tYmEqB2/pB75KVDJ5xnH46Tt9pmzz+0l\n"
                    + "4/PbEOuUz4YKRfxjLr6lgaL1OIw9FHkw9pxnw4K8WucxhveZDB/SFLchooDtwfcb\n"
                    + "maGBAoIBAQDW9CBUKqV2sSc0WTMQvu5TWj8zg1RQnJQRsSAf7N9mY4l4GZMU6Dsb\n"
                    + "W5475zyVMNISwmi8QXn39hcseFnBr8bIrWiFLwloBGIFaVi/IxOqpVrvkuDWCBgJ\n"
                    + "JfIaOLWw6noStehjMt6cs2FibMmqhKg+YffMBt042Schb/DejYE8dJthSVnWSdSM\n"
                    + "qNWNjziac2zHahVjOy9UxPBjHwuAEGrZ8VTtF8x8+d5Z2NIZVOl/E6RTo6jqUsZe\n"
                    + "Qp8MIQ/6fhCvZDIMyMSESXVftDCRpVT8aUQtGXKNWzBIOcVlDRnzSlW6cALMUp/L\n"
                    + "0tFCbgOHhooGdsPPkMsmktDozO5Sl0ZK\n"
                    + "-----END PRIVATE KEY-----";

            System.out.println("PrivateKeyCert : " + privateKeyCert);
            System.out.println("publicKeyCert : " + publicKeyCert);
            String payload1 = "{\"Host\":\"pivot.com\",\"endToEndId\":\"1122719657501044799\",\"originatorDetails\":{\"account\":{\"accountCurrency\":\"SGD\",\"accountNumber\":\"3523095739\",\"accountType\":\"D\"}},\"paymentAmount\":{\"amount\":1.00,\"currency\":\"SGD\"},\"purposeCode\":\"OTHR\",\"receiverDetails\":{\"account\":{\"accountCurrency\":\"SGD\",\"accountNumber\":\"147125793003\",\"accountType\":\"D\"},\"accountName\":\"Saxo Capital Markets Pte.Ltd.\",\"bic\":\"HSBCSGS0XXX\", \"proxyType\":\"\", \"proxyValue\":\"\"},\"transactionReference\":\"112271965750104474\"}";
            String payload2 = "{\"accounts\":{\"accountInformation\":[{\"accountNumber\":\"3523095739\",\"accountCurrency\":\"SGD\",\"accountType\":\"D\"}],\"uen\":\"201716150D\"}}";
            final PrivateKey privateKey = SecurityKeyUtils.parseRSAPrivateKey(privateKeyCert);

            System.out.println("payload1 : " + payload1);
            System.out.println("payload2 : " + payload2);
            HashMap<String, Object> claims = new ObjectMapper().readValue(payload1, HashMap.class);
            String token = Jwts.builder().setPayload(payload1).setHeader(getJWTHeader()).signWith(SignatureAlgorithm.RS256, privateKey).compact();
            String token2 = Jwts.builder().setPayload(payload2).setHeader(getJWTHeader()).signWith(SignatureAlgorithm.RS256, privateKey).compact();

            System.out.println("token : " + token);
            System.out.println("token2 : " + token2);

            String encryptedPayload = "zfobzsGBzniY8pXTqCq+AP5n2yT0O0XIxz3sERqxzWQPPkoC3X9uwnyW7/1q52MnPh/gHJmhva355Ty+toKg6YISTCHUux83dC+HwW6KUi534DdraGImlbEBxY5FQ5Q2z/qjXUO6o2zFnxCq6WAg4n6pkIk0SFYJUzxlxOk27dEfPDaCNJGxdZVx5mAFB5sNC3moKeYIl5AiNA+isaVZBx2TSkpiIIbPwyN0CgD/54jOlep/ziHQNGfVoetbvub2VbAvmAc6GwV88niW2qTKADmpP8B9CRTGQYacWiwd4DnodLD5w90Z2n2+RANefY/Hy8k+lMcQVbhLk5mIjZOTLg+L5er1UpIkq3rSi0iptpqPc6PL0qbGJcWC8B3vSFZPE5vYq19p111r+mCdI8L93kELH7BS2DyuJ14OuHvXpEt4b9W4LL6QydxI03fcs0blgHaGNs7xCE9z/rSGlOBaJhwIjIs9/Gwm83ZhQSTrwBNc/CFDN2tqi5Ft+8TIfhm4OHwA+gDreaKJOWD+VGfu/O4ZNAljPyCsKIKsQVxjEBbPwT4HNwzn+PS1nmgoTb0kw1U3YY7dP9ExpYet7j7eSdW921BgCVgCt4aPGgvEcQzZdy2jE+xoHpfODbDOVg/5JpVTSkHEwdiiWYz3zfnhAnXehHgwrWtSEfx4NAqAqWtQO4gvPfxrhX3HvXOGR1EpkcTEuR03ErbPsiMJIlCNCumDv7vh2GlkSqTkva0eRaiP704RvB25O7Xi0ULVtcKMMbcGTHeLIDFfNvuhJyi9ACIJWfMHfQQ3lxXOu4HzioBa7FMMp4NbboSZ5acxn9rxXvGNcGHwos+zbNTSj0PJL6hBkDRJoKkHe87IYM75EW6Xd0In83z2B4NYQKk8YcNE27lgoDNpBJKc+UBMO/qD20NJxvSVbKBA/ok5PAVCB2eeh5bXIl5PlwmJjMLie4MQ";
            String encryptedSessionKey = "X1BTT6zA8wQOqz78MEvE1wDzYAr81pt+M6AsHKfbeX2dU2Vb0BY9VB6jwO9JwfM4yPgbV8pTF+uxm/dv8xuVcsDPIOBICu8JpZ49zmM7IuAhDnznFtc61IVtZnoFH0goU1VR56f08ZBPrJflzOOuPVP1vjEXdpnrMsjQ49VqPVySKuN+jo++UyCb482cDa6hT6sUX3GKHTj6WYAoTGYA+kx5em3SyF7+CtbnAl/GlzWgoY1qzF3sLJDp25Pirw6bAymLDA7IhCRJeGK1GYXpW2/DFksJ+uBFJ7doQ6uBpuQ0snCR9bVGWrWmnqkrjmU3R8Tar8KAXf1kTaZjJErBTw41GGP9X6ojsHs6jUbEMgUGKX/a39PoB4z7W/3Pa//ImpJjdAtk4iCk1a23DB12BWsKTx+rzQpXpPENllO8C+N24pMqMuMav10D5BmsZ9KTI2ifw4NPstdc6LtcbS+KKboNyuZQAKsBmyrV2uswJb7m1lF4qju4y2aCzbkmlaTzDbau4LCKg38L60Ft/c41NGCarM6VFHA4AQ6onf+w6Oa5OPH+VLjK9r6DUL3z0Y9z3/qPHHuXllvrT77DrXoAiPGAupJby73Eo1hxZJkfZal8YiHPbgx2ewvNm/y6ItkIaqXZENHBRZ+t+nA0pE38+dFv6eeIN0fkpsVRsG/XuOo=";
            String iv = "w6Js+7SQY7WhexiSk/F9NjM8BNR0SasqmsIEZ2REcOoJSN8goyKk00t4/KxP4SF2KP1hfTuO6yJmMfia2OxMyDepNKMAL9oh5aaNR8bmB7Ak/ofpVeNO/vklV1P5464b";
            String payloadSignature = "TWXF4NhLTfFL3cZSoYfNLNm/5plj82DtmEKZVia+bUdhgQloQjRT1pzq2eZAwRf70BRszgAI9QPMzfemNWQVx7BNjFom8El9D5/J93i35Es131Wu05xXVzVQaV/nJ+NkcaSWFbuJNtUs6knpjefW3RvBWowCb9Rq9q6zXlXz4/IenHheXkxdS78+ePuJasLulfnY7fNQJzolCsDedji29Wc2rwoF4BYRW6ZyJQ040ePwE0N7tR16J1xDdW9ySD98i6UEs/QJWHfoyBgm3JgZJpck1TD5hZbq29pMxNcLiJgEOGQmm0OKX2rHYt1Tn0T50t6Y/bwc60OJRXK9X/YmEw==";

            String privateKeyCert2 = "-----BEGIN PRIVATE KEY-----\n"
                    + "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQDoFG+HzokEUQIB\n"
                    + "i9vy8YrDDXU/awzEAWbPWTAWdMwRz1/Nn2Ib4FcxaGCKI5clOnjYQu749iKGcAzc\n"
                    + "c4fuCSVYWCvEmt8KONrjboCbLIw//vXMsvRFuOKzJ1exjLpL4pqdf1e84mLlCz6v\n"
                    + "fQORxS5xgdoAjjKycVPbahtWQokmfEpuZgc6Fy9HanZJE/1pU0HPrRZCvQ0PlYsd\n"
                    + "rqyhYYlV4zt7MmBwdumpPkv5nsNsBQTObMBSNyws+XG4XZz985Yf/Mn/KHVjlHvd\n"
                    + "gsU6IMj8liFn391eHNlLenRpScEV3hO86F53P3Y0oVmLAWdnA9YHsBc3XtCEcLpl\n"
                    + "IWFx0PG+bnVzitBMrsP3uk/1EJAi4WdH5XyBsK6g9Qm9itEZr0HD1JXJdXI68Evi\n"
                    + "pNzyHSOJCH5dYqq7pLqGEowlaYg/J/gIz13YuwuaEJ966R/P+5y5WbI11w2jdYER\n"
                    + "+PXkp1vHa14daMYAKjXJTe9qkYNSDPJLIqxDEwpguZryjanrdgapcMQelelctZ9l\n"
                    + "A8sQ6SeuMrGwh1b3MeVo6+p8gCJtQXp/WcOuW6Go1GQKz1NONBfTbsnXhzLOnrzR\n"
                    + "zFpZVW//dbPLHggpfMGPI4bq+rncFr+t2W7/o49wVUNv/gS0ggT3IU+50requCvm\n"
                    + "rGtbLrx4XCvlKS56TZjzHLlCkPb+6wIDAQABAoICADSfTZqxjgfYhmfrWnf/pMMN\n"
                    + "biRvFXnsB2/EfU7aNNtk9ggdt5UyjRK65AjugPsEAxqmMqFozfNFvOTPnWav1DPv\n"
                    + "6hyuMRyUP62KBMcqlrJ8ESfU++ZDQcP1i8DKdAN44i4MDczaWCovHouUARcwapN6\n"
                    + "KgAuPfiQEb+Sk5g/hfGgBRB7OfBGCYZb8W4ohjE3m5iHWqDPVQLPmbelP6doAkfj\n"
                    + "Oj148r0NeXXSlk+HXDBGAwz7Q7BgqjeYZ9yx/ULn1IZ0PykH93YY2O0lFatUe74q\n"
                    + "HHD9JaiNSgf/QB4PhyR6MmeHGMRZM2hDnZYTP2rwR7t+hgUcDpO5K8p6gynXgriI\n"
                    + "sAeqmP+avBGHAULs5SgTp5qfYSy3yhjRQshsyYSD2u14CTbDFxD/UnJZQf+vXeG+\n"
                    + "HvFUOOHGeDseYkVg4lXZhOHJEYl/XJG4QUKI3PA3oulK3ld8dosZ7gO+h4kYRfu5\n"
                    + "xGp5lgN5uGRQwXFXd5IV0wkhijRNlQOqfTnumH7x/XiULwhIHCllTEeztwL/pz4B\n"
                    + "ZOdLJ7p6uGYqQCfYeiWsmH9wDcMzMczlRE3BaHggxaz0ma0xFPpQ+8as2OEkH2TT\n"
                    + "rs4Zb8FaBdWnF9ckqmdzR240dZugCV35rUNyWUbI71pI6POIsyQKIPDZTSrlj10N\n"
                    + "s5Ge31G4JTPiP+iMG7NBAoIBAQD2evGz+GfJlgnp/4L37juhLlxzUDHancYMc/mE\n"
                    + "qRInTahUL04YK+txpq+Ceq2NcpqBN4nYYyEoXWA1fuS2/DH3LbVvyD9DA2qHnsOx\n"
                    + "OZrUpfqhtPPB6cDcCVkwioWiL2KQbutz/pZSYM5QFK8osCk4+7FzND+qTGWHgunW\n"
                    + "O8T2b2de+dRq0ZqOUMbNtxLgbAdKKBOy0JvZEiHUCtNr4tw2Srct/uwle3iEY7Dj\n"
                    + "dL9j2PtkkZVgpoqm9dbHE/TCIOyXyxUbB59PBRciiw0bmTWdGrKNK0Z8FTgNWktc\n"
                    + "ayTVyxZ38Xv1TSJOkdAqaITm5BFKu8k6m+FFgfbHpmMqyOqDAoIBAQDxCxu9D0J6\n"
                    + "uChVia0vBtbnv2R4GlMgk+lAukJGNYNoQ76PYF6frBa9o3K+QHxaz3CPq/7UdR5S\n"
                    + "S1eAnqgTpVz9nLgnpCoGtWgG6yrzRi4usl7VoHcGoleZp7keOXynnBjIds41i7gh\n"
                    + "FdvBdYlL3fObBkLHvIv9+mTGKmz7vzsPQrr6THPN/O0ycQ5COndWoaT3yREUfXB1\n"
                    + "yWiJrDe2Ju2/wwqo0bn+qNcXIU6XNbPL8Scb9AG9xIGbvyF2KC0V8+O/mFTYPv4x\n"
                    + "Mhp6K6bNzfTdlT6f7J6qHQTkASxbF7cwW7nKu9BFFFBd8HMVgyTKdZwcPvzzCRzL\n"
                    + "4ZR+oULV8o15AoIBAQDw/EuNNnXjNnsC+frHBXnvoTBq3iGgKVoVmt5+wf8pxlep\n"
                    + "14Z3Q5mzSNGYtPEytrYxWGfB1Un5+TGznBlqfzErBtm6LQMNNi3V43hZsoSbHJzr\n"
                    + "tcwN78nMWKeHOihl5gZsSMaUZnn6mQOecFrBYVssh4l7pb4JdyBdWbXe7BkrCMj8\n"
                    + "py70MXL3aiq1vyUJnxWRf5Wi4ys9FOlWNC958Bh2TJ7ErdWc4Bd4+S9ddCwcJ23D\n"
                    + "akOud6Zgj0Qq6GwJ0b8AumIsR0uPKt7vuxxZu3Yb4JpqQ8FqWCSxiXLdLTSagzG4\n"
                    + "m7YipAPC5fWtQPuP87SkaO3aC8Y4+1SY5ylWD5KZAoIBAQC3VgKgFxYybxXGkbQW\n"
                    + "u7YDiLBrJWKqubXFFKZJ7nPnfvdW6C6wddzKGqKwkC3FiFgN/v0RLY+Femawnnwq\n"
                    + "jTEgeO4cwKFyftIDySTe6jdAL00wYR+G/T1x0n9bvM/SUEkNWEfZtE61Y4HFMJfg\n"
                    + "C445EFcEwqUb5TJ3A1fnVvkdz1yV+kZum3zyS8lTmFKiosnt6ZHDKvG5tRp89G9f\n"
                    + "bVaR71CRPQuh5VvL7PpwyljZuq9LdJlKdEc8ZfCWhVJdiyZj2QJXg/olAv3ehIsX\n"
                    + "qBoh211JmFjJ/M52eWn6UxxzfKDg0V9E24GRIcz2xMvb7f5yAXq1g+3SKN6gPhmu\n"
                    + "zxDJAoIBAQDE3Ec5vYG407oBu9pGFp2Hcb37WV0aqAGEjEqkU9VwKaPuZDYUTS4K\n"
                    + "3urDX0JAhOpMleRBZtH0rhUI+nZO2ogqwiOPDZ+A4K0HVHIG7yeyMfSJ8IVb3ynX\n"
                    + "0x7sIkqoHC/RonSqvd3fsi8HS5zB+Y8mFH8TAuTuAG6W3DpId5Da2KPHTZqhvrqe\n"
                    + "30Vw/8CwORSRIxwmJPi3nVq5O/pY2LgpYnq4vHQfAIy4g1U6Uz5K1BtectJsHN0A\n"
                    + "ZUX9c0Oe+Crn5jXXcWay4rFNRfJh2WKO1HFQoTlPs9engPoA/B/hwIxIXEoxibUT\n"
                    + "P0sWGpiTpAl4Pe62PApi14K2J+fbIHnO\n"
                    + "-----END PRIVATE KEY-----";
            final PrivateKey privateKey2 = SecurityKeyUtils.parseRSAPrivateKey(privateKeyCert2);
            String decryptedSessionKey = SecuredRSAUsage.rsaDecrypt(java.util.Base64.getDecoder().decode(encryptedSessionKey.getBytes()), privateKey2);
            System.out.println("decryptedSessionKey : " + decryptedSessionKey);
            byte[] decodedSessionKey = Base64.getDecoder().decode(decryptedSessionKey);
            SecretKey secretKey = new SecretKeySpec(decodedSessionKey, 0, decodedSessionKey.length, "AES");
            System.out.println("secretKey : " + secretKey.toString());

            byte[] ivBytes = Base64.getDecoder().decode(iv);

            GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, ivBytes);
            System.out.println("gcmParamSpec : " + gcmParamSpec);

            // Errors Here
            byte[] returnStr = SecuredGCMUsage.aesDecrypt(Base64.getDecoder().decode(encryptedPayload), secretKey, gcmParamSpec, "aham.ai".getBytes());
            System.out.println(new String(returnStr));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static Map getJWTHeader() {
        Map header = new HashMap();
        header.put("alg", "RS256");
        header.put("typ", "JWT");
        return header;
    }

}
