-- Adminer 4.7.6 PostgreSQL dump

DROP TABLE IF EXISTS "pontos";
CREATE TABLE "public"."pontos" (
    "id" bigint NOT NULL,
    "nome" character varying(50) NOT NULL,
    "latitude" double precision NOT NULL,
    "longitude" double precision NOT NULL,
    CONSTRAINT "pontos_nome_key" UNIQUE ("nome"),
    CONSTRAINT "pontos_pkey" PRIMARY KEY ("id")
) WITH (oids = false);


-- 2020-03-25 13:07:48.714184+00
