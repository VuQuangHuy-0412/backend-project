# backend-project
## Description
- Service phục vụ cho việc xử lý các nghiệp vụ của hệ thống phân công giảng dạy và phân công hướng dẫn đồ án trong trường đại học  
- Các tính năng chính: quản lý  dữ liệu đầu vào, phân công giảng dạy, phân công hướng dẫn, theo dõi kết quả phân công

## Require
- java adopt open JDK 17
- mysql 8 master-slave

## Service
port service: 8084

## CI/CD
- prod domain: [https://sc5-backend.university-timetabling.com](https://sc5-backend.university-timetabling.com)

## Local environment
- run: docker-compose up
- build: `gradle clean build`

## Các thư viện thứ 3 được sử dụng
- org.springframework.boot 2.3.4 (security, web, actuator, data-jpa, starter-data-redis)
- org.redisson 3.13.6 : cache redis
- org.apache.commons  1.3.2 : lib util java
- io.jsonwebtoken:jjwt-api 0.11.2 : jwt
- io.jsonwebtoken:jjwt-impl  0.11.2 : jwt
- io.fusionauth  3.5.3 : verify jwt
- commons-codec  1.15 : java common
- net.logstash.logback 1.5.5 : log json
- org.projectlombok:lombok 1.18.12
- io.micrometer 1.5.5 monitor trace 
