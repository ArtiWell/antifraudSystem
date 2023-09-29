INSERT INTO STATUS(id, status, price)
select 1, 'ALLOWED', 200 from dual
where NOT EXISTS(select id from STATUS where ID = 1);

INSERT INTO STATUS(id, status, price)
SELECT  2, 'MANUAL_PROCESSING', 1500 from dual
where NOT EXISTS(select id from STATUS where ID =2);

INSERT INTO STATUS(id, status, price)
SELECT  3, 'PROHIBITED', null from dual
where NOT EXISTS(select id from STATUS where ID = 3);

