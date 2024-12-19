package com.axisbank.builder.authentication.RequestResonseEncryption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EXTERNAL_SERVICE_STATUS")
public class ServiceExtraFlag implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  protected Long id;

  @Column(name = "SERVICE_NAME")
  String service;

  @Column(name = "STATUS")
  int flag;
}
