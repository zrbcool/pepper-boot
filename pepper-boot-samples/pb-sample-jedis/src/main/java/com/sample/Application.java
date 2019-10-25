package com.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-24
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    private Jedis jedis;

    @Component
    class Runner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) throws Exception {
            if (jedis != null) {
                //b
                System.out.println(jedis.set("hello", "pepper boot!"));
                TimeUnit.SECONDS.sleep(1);
                System.out.println(jedis.get("hello"));
            }
        }
    }
}
