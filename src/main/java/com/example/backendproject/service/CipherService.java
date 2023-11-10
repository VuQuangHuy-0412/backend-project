package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.util.AES;
import com.example.backendproject.util.CommonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CipherService {
    @Value("${ewallet.secretKey:}")
    private String secretKey;

    @Value("${ewallet.secretIV:}")
    private String secretIV;

    @Value("${hash_salt:+WW=2c*8eW#da*5#&8M#}")
    private String hashSalt;

    public String encrypt(Object obj) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(secretIV)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu cấu hình secretKey");
        }

        String json = CommonUtil.toJson(obj);
        String token = AES.encrypt(json, secretKey, secretIV);
        return token;
    }

    public <T> T decrypt(String token, Class<T> returnType) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(secretIV)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu cấu hình secretKey");
        }

        String json = AES.decrypt(token, secretKey, secretIV);
        T obj = CommonUtil.fromJson(json, returnType);
        return obj;
    }

    public String hash(String input) {
        input = input + hashSalt;
        return DigestUtils.sha256Hex(input);
    }

    public String adminHash(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }

    public boolean check(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }
}
