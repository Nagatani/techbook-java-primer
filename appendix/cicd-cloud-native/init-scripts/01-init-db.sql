-- Initialize development database
CREATE DATABASE devdb;
CREATE USER dev WITH PASSWORD 'devpass';
GRANT ALL PRIVILEGES ON DATABASE devdb TO dev;

-- Create test database
CREATE DATABASE testdb;
GRANT ALL PRIVILEGES ON DATABASE testdb TO dev;