package org.example.abe_test.service;

import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class AbeService {

    private final Random random = new Random();

    /**
     * 模拟私钥生成算法
     * 私钥结构分为SK1和SK2两部分，SK1作为TK(token)
     */
    public String generatePrivateKey(List<String> attributes) {
        // 模拟生成私钥
        StringBuilder privateKey = new StringBuilder();
        privateKey.append("SK1:").append(generateRandomString(32)); // TK部分
        privateKey.append("|SK2:").append(generateRandomString(32)); // SK2部分
        
        // 添加属性信息
        privateKey.append("|ATTRS:");
        for (String attr : attributes) {
            privateKey.append(attr).append(",");
        }
        
        return Base64.getEncoder().encodeToString(privateKey.toString().getBytes());
    }

    /**
     * 从私钥中提取TK(token)
     */
    public String extractToken(String privateKeyBase64) {
        try {
            String privateKey = new String(Base64.getDecoder().decode(privateKeyBase64));
            String[] parts = privateKey.split("\\|");
            for (String part : parts) {
                if (part.startsWith("SK1:")) {
                    return part.substring(4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模拟加密算法
     */
    public String encrypt(String plainText, String accessControlStructure) {
        // 模拟加密过程
        String encrypted = "ENCRYPTED_" + Base64.getEncoder().encodeToString(plainText.getBytes());
        return encrypted;
    }

    /**
     * 模拟解密算法
     */
    public String decrypt(String cipherText, String privateKey) {
        try {
            // 模拟解密过程
            if (cipherText.startsWith("ENCRYPTED_")) {
                String encoded = cipherText.substring(10);
                return new String(Base64.getDecoder().decode(encoded));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模拟等值测试算法
     */
    public boolean equalityTest(List<String> cipherTexts, List<String> tokens) {
        if (cipherTexts.size() != tokens.size()) {
            return false;
        }
        
        // 模拟等值测试逻辑
        // 这里简化处理，实际应该根据ABE算法进行等值测试
        for (int i = 0; i < cipherTexts.size(); i++) {
            String cipherText = cipherTexts.get(i);
            String token = tokens.get(i);
            
            // 简单的模拟测试
            if (cipherText == null || token == null) {
                return false;
            }
        }
        
        return true;
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
//     fun keygen(mpk:MasterPublicKey, msk:MasterSecretKey, attribute:List<Boolean>): SecretKey{
//         //检查attribute合法,即长度为N，1代表具有该属性，0代表不具有该属性
//         val pairing = mpk.pairing
//         //提取g_alpha, g_alphaPrime, g_a, h_i
//         val g = mpk.g
//         val g_alpha = msk.g_alpha
//         val g_alphaprime = msk.g_alphaprime
//         val g_a = mpk.g_a
//         val h = mpk.h
//         val N = h.size
//         //选取随机数z，z‘,保证任意h_i^z和h_i^z'不等于g
//         var flag = true
//         var z = pairing.zr.newRandomElement()
//         var zPrime = pairing.zr.newRandomElement()
//         while (flag){
//             flag = false
//             for(i in h.indices)(
//                 if (h[i].powZn(z) == g || h[i].powZn(zPrime) == g){
//                     z = pairing.zr.newRandomElement()
//                     zPrime = pairing.zr.newRandomElement()
//                     flag = true
//                     break
//                 } else{}
//             )
//         }
//         //计算SK，Sk‘
//         val k = g_alpha.mul(g_a.powZn(z))
//         val k_prime = g_alphaprime.mul(g_a.powZn(zPrime))
//         val l = g.powZn(z)
//         val l_prime = g.powZn(zPrime)
//         val ks = List(N){index ->
//             if (attribute[index])
//                 h[index].powZn(z)
//             else g
//         }
//         val ks_prime = List(N){index ->
//             if (attribute[index])
//                 h[index].powZn(zPrime)
//             else g
//         }
//         val sk = SecretKey(
//             k = k,
//             l = l,
//             ks = ks,
//             k_prime = k_prime,
//             l_prime = l_prime,
//             ks_prime = ks_prime
//         )
//         return sk
//     }

//     fun enc(mpk:MasterPublicKey, acc:AccStru, message: ByteArray, t:Int): CT{
//         //获取矩阵M的行数与列数
//         val l_cal = acc.M.size
//         val n = acc.M.first().size
//         //计算f（x）系数
//         var temp = message + TypeTrans.int2Bytes(t)
//         val f = List(t){index ->
//             val hashResult = mpk.H1(temp) // 计算哈希
//             temp += hashResult            // 将哈希结果拼接到 temp 后
//             val hashResult_Element = mpk.pairing.zr.newElementFromBytes(hashResult).immutable
//             hashResult_Element// 将此哈希值作为本次迭代的结果，存入列表 f
//         }
//         //随机选取向量v，元素u，元素v_cal,向量r
//         val v = List(n){
//             mpk.pairing.zr.newRandomElement().immutable
//         }
//         val u = mpk.pairing.zr.newRandomElement().immutable
//         val v_cal = mpk.pairing.zr.newRandomElement().immutable
//         val r = List(l_cal){
//             mpk.pairing.zr.newRandomElement().immutable
//         }
//         //计算lambda_i
//         val lambda = List(l_cal){index ->
//             Tools.InnerProduct(acc.M[index], v)
//         }
//         //计算密文
//         val A = TypeTrans.ByteArray2element(message, mpk.pairing).powZn(v_cal)
//         val D = List(l_cal){index ->
//             mpk.g_a.powZn(lambda[index]).mul(mpk.h[acc.rou[index]].mul(r[index]).invert())
//         }
//         val E = List(l_cal){index ->
//             mpk.g.powZn(r[index])
//         }
//         var F_Bold = D[0].toBytes() + E[0].toBytes()
//         for( i in 1 until D.size){
//             F_Bold += D[i].toBytes() + E[i].toBytes()
//         }
//         val c1 = mpk.g.powZn(u)
//         val c2 = mpk.g.powZn(v[0])
//         val c3 = Tools.XorByteArrays(A.toBytes() + functionF(f,A),mpk.H2(mpk.e_g_g_alpha.powZn(v[0]).toBytes()))
//         val c4 = Tools.XorByteArrays(message + u.toBytes() + v_cal.toBytes(),
//             mpk.H3(mpk.e_g_g_alphaprime.powZn(v[0]).toBytes() + c1.toBytes() + c2.toBytes() + c3 + F_Bold))
//         val c5 = mpk.H4(TypeTrans.int2Bytes(t) + c1.toBytes() + c2.toBytes() + c3 + c4
//                 + F_Bold + mpk.e_g_g_alphaprime.powZn(v[0]).toBytes() + TypeTrans.ElementList2Bytes(f))
//         val ct = CT(
//             t = t,
//             c1 = c1,
//             c2 = c2,
//             c3 = c3,
//             c4 = c4,
//             c5 = c5,
//             D = D,
//             E = E
//         )
//         return ct
//     }
// /*
//     fun dec(mpk:MasterPublicKey, ct: CT, acc: AccStru, sk:SecretKey): ByteArray{
//         //从私钥中提取解密用户的属性
//         val attribute = List(sk.ks.size){index ->
//             if(sk.ks[index] == mpk.g){
//                 false
//             }else{true}
//         }
//         //计算集合I（如果访问结构中rou_i是解密用户的属性之一，那么i是I的元素）
//         val I = acc.rou.indices.filter { i ->
//             val j = acc.rou[i]
//             attribute.getOrNull(j) ?: false
//         }
//     }

//     fun authorize(sk:SecretKey): TK{}

//     fun test(mpk: MasterPublicKey, cts: List<CT>, tks: List<TK>): Boolean{}
//     */
//     private fun functionF(f: List<Element>, A: Element): ByteArray{
//         var result = f[0]
//         var x = A
//         for (i in 1 until f.size) {
//             result = result.add(f[i].mul(x))
//             x = x.mul(x)
//         }
//         return result.toBytes()
//     }

//     // SHA-256 哈希函数辅助方法
//     private fun sha256(input: ByteArray): ByteArray {
//         val digest = MessageDigest.getInstance("SHA-256")
//         return digest.digest(input)
//     }
// }
