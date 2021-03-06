package com.ob.modelexample.business.domain;

import com.ob.common.base.domain.BaseDomain;
import com.ob.common.base.domain.IdStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author: oubin
 * @date: 2019/3/28 14:12
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_student")
@GenericGenerator(name = "id", strategy = IdStrategy.UUID)
public class Student extends BaseDomain<String> {

    @Column(name = "name")
    private String name;

    @Column(name = "count_student")
    private int countStudent;

    @Version
    @Column(name = "version")
    private int version;
}
