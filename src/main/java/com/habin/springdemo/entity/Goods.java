package com.habin.springdemo.entity;

import com.habin.springdemo.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 제품 관리
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "EDU_GOODS")
public class Goods extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 제품ID
	 */
	@Id
	@Column(name = "ID", nullable = false)
	private String id;

	/**
	 * 제품명
	 */
	@Column(name = "NAME")
	private String name;

	/**
	 * 상세설명
	 */
	@Column(name = "REMARK")
	private String remark;

	/**
	 * 사진경로
	 */
	@Column(name = "PICTURE_PATH")
	private String picturePath;

	/**
	 * 제조일자
	 */
	@Column(name = "MF_DATE")
	private LocalDateTime mfDtm;

}
