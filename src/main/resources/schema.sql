create table if not exists role
(
    id   bigserial
        constraint role_pk
            primary key,
    name varchar not null
);

alter table role
    owner to postgres;



create table if not exists users
(
    id       bigserial
        primary key,
    login    varchar not null,
    password varchar not null,
    role_id  bigint  not null
        constraint users_role_id_fk
            references role
);

alter table users
    owner to postgres;




create table if not exists uploaded_file
(
    id       bigserial
        primary key,
    name          varchar                                         not null,
    size          bigint                                          not null,
    link          integer,
    user_id       bigint                                          not null
        constraint uploaded_file_users_id_fk
            references users,
    creation_date timestamp                                       not null
);

comment on column uploaded_file.size is 'size in bytes';

comment on column uploaded_file.link is 'hashCode of uploaded file';

alter table uploaded_file
    owner to postgres;





