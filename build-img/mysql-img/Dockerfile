FROM mysql:8.0.33

# Copy tệp vtesdb vào thư mục docker-entrypoint-initdb.d trong container
COPY ./vtesdb /docker-entrypoint-initdb.d/

# Thiết lập quyền truy cập cho thư mục docker-entrypoint-initdb.d
RUN chmod -R 755 /docker-entrypoint-initdb.d/

# Thiết lập biến môi trường
ENV MYSQL_ROOT_PASSWORD=AdminDef \
    MYSQL_DATABASE=vtes

# Chạy các tệp SQL trong thư mục docker-entrypoint-initdb.d
RUN /etc/init.d/mysql start && \
    sleep 5 && \
    cat /docker-entrypoint-initdb.d/*.sql | mysql -u root -p${MYSQL_ROOT_PASSWORD} ${MYSQL_DATABASE} && \
    /etc/init.d/mysql stop
