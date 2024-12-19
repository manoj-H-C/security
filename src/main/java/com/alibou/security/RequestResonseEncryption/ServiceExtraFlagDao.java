package com.axisbank.builder.authentication.RequestResonseEncryption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceExtraFlagDao extends JpaRepository<ServiceExtraFlag, Long> {

  public ServiceExtraFlag findByService(String service);
}
