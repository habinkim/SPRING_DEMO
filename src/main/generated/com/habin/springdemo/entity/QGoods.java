package com.habin.springdemo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGoods is a Querydsl query type for Goods
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGoods extends EntityPathBase<Goods> {

    private static final long serialVersionUID = 1203565820L;

    public static final QGoods goods = new QGoods("goods");

    public final com.habin.springdemo.entity.base.QBaseEntity _super = new com.habin.springdemo.entity.base.QBaseEntity(this);

    public final StringPath id = createString("id");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> insDtm = _super.insDtm;

    //inherited
    public final StringPath insUserId = _super.insUserId;

    public final DateTimePath<java.time.LocalDateTime> mfDtm = createDateTime("mfDtm", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath picturePath = createString("picturePath");

    public final StringPath remark = createString("remark");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDtm = _super.updDtm;

    //inherited
    public final StringPath updUserId = _super.updUserId;

    public QGoods(String variable) {
        super(Goods.class, forVariable(variable));
    }

    public QGoods(Path<? extends Goods> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGoods(PathMetadata metadata) {
        super(Goods.class, metadata);
    }

}

