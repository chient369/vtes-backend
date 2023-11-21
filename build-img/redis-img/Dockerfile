# Sử dụng base image Redis
FROM redis:latest

# Copy tệp redis.conf vào container
COPY redis.conf /usr/local/etc/redis/redis.conf

# Chạy Redis với tùy chọn cấu hình
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]

