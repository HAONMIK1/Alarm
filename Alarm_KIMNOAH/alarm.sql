Drop TABLE AlertTable;
CREATE TABLE AlertTable (
    LOCATION VARCHAR2(255) NOT NULL,
    GRADE NUMBER,
    TIME VARCHAR2(255) NOT NULL,
    ALERT_CHECK VARCHAR2(255)
);



select * from AlertTable  ;

commit;