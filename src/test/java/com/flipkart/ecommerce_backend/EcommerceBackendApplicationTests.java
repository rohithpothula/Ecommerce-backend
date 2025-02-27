package com.flipkart.ecommerce_backend;

import com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcommerceBackendApplicationTests {

  @Autowired private LocalUserRepository localUserRepository;

  @Test
  void contextLoads() {}

  @Test
  public void repoTest() {
              String className = this.localUserRepository.getClass().getName();
    String packageName = this.localUserRepository.getClass().getPackageName();
    // for now make test fail
    //assert (className.equals("com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository"));
  }
}
