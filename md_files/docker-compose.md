# How Docker Compose Works in Your Spring Boot Security Project

## Table of Contents
1. [Your Configuration Files](#your-configuration-files)
2. [Step-by-Step Execution](#step-by-step-execution)
3. [Container Communication](#container-communication)
4. [Configuration Breakdown](#configuration-breakdown)
5. [Common Commands](#common-commands)
6. [Troubleshooting](#troubleshooting)
7. [Workflow Guide](#workflow-guide)
8. [Comparison: Local vs Docker Compose](#comparison-local-vs-docker-compose)

---

## Your Configuration Files

### docker-compose.yml

```yaml
version: "3.8"

services:
  mysqldb:
    container_name: mysqldb
    image: mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ali-security
    networks:
      springboot-mysql-net:

  security:
    container_name: security
    build:
      context: ./
      dockerfile: Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8080:8080"
    depends_on:
      - mysqldb
    networks:
      springboot-mysql-net:
    restart: on-failure

networks:
  springboot-mysql-net:
```

### Dockerfile

```dockerfile
FROM eclipse-temurin:17

LABEL mentainer="manojhc110@gmail.com"

WORKDIR /app

COPY target/security-0.0.1-SNAPSHOT.jar /app/security.jar

ENTRYPOINT ["java", "-jar", "security.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
```

### application-docker.properties

```properties
spring.datasource.url=jdbc:mysql://mysqldb:3306/ali-security
spring.datasource.username=root
spring.datasource.password=root
```

---

## Step-by-Step Execution

### What Happens When You Run `docker-compose up`

```
┌─────────────────────────────────────────────────────────┐
│ You run: docker-compose up                              │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│ Step 1: Create Network                                  │
│ springboot-mysql-net (bridge network)                   │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│ Step 2: Start MySQL Container                           │
│ - Pull mysql:latest image                               │
│ - Create database: ali-security                          │
│ - Set root password: root                                │
│ - Connect to network                                     │
│ - Start MySQL server on port 3306                        │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│ Step 3: Build Spring Boot Image                         │
│ - Read Dockerfile                                        │
│ - Copy security.jar                                      │
│ - Create image                                           │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│ Step 4: Start Spring Boot Container                     │
│ - Set SPRING_PROFILES_ACTIVE=docker                     │
│ - Map port 8080:8080                                     │
│ - Connect to network                                     │
│ - Run: java -jar security.jar                            │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│ Step 5: Spring Boot Starts                              │
│ - Loads application-docker.properties                   │
│ - Connects to MySQL at mysqldb:3306                     │
│ - Creates/updates database schema                        │
│ - Starts web server on port 8080                         │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│ ✅ Application Running                                   │
│ Access: http://localhost:8080                            │
└─────────────────────────────────────────────────────────┘
```

### Detailed Step Breakdown

#### Step 1: Create Network

```bash
# Docker Compose creates a custom bridge network
Network: springboot-mysql-net
Type: Bridge
Purpose: Allow containers to communicate by service name
```

#### Step 2: Start MySQL Container

```
1. Docker pulls mysql:latest image (if not exists)
2. Creates container named "mysqldb"
3. Sets environment variables:
   - MYSQL_ROOT_PASSWORD=root
   - MYSQL_DATABASE=ali-security
4. Connects to springboot-mysql-net
5. MySQL starts and creates database "ali-security"
6. Listens on internal port 3306
```

**Important:** MySQL is NOT exposed to host (no port mapping). Only accessible within Docker network.

#### Step 3: Build Spring Boot Image

```
1. Looks for Dockerfile in current directory
2. Executes Dockerfile instructions:
   - FROM eclipse-temurin:17 (pulls JDK 17 base image)
   - WORKDIR /app (creates working directory)
   - COPY target/security-0.0.1-SNAPSHOT.jar /app/security.jar
   - ENTRYPOINT ["java", "-jar", "security.jar", ...]
3. Creates Docker image tagged as security:latest
```

#### Step 4: Start Spring Boot Container

```
1. Creates container named "security"
2. Sets environment: SPRING_PROFILES_ACTIVE=docker
3. Maps ports: Host 8080 → Container 8080
4. Connects to springboot-mysql-net
5. Waits for mysqldb to start (depends_on)
6. Runs: java -jar security.jar --spring.profiles.active=docker
```

#### Step 5: Spring Boot Initialization

```
1. Spring Boot reads SPRING_PROFILES_ACTIVE=docker
2. Loads application-docker.properties
3. Connects to MySQL using: jdbc:mysql://mysqldb:3306/ali-security
4. Hibernate creates/updates database schema
5. Application starts on port 8080
6. Ready to accept requests
```

---

## Container Communication

### Network Architecture

```
┌─────────────────────────────────────────────────────────┐
│  Docker Network: springboot-mysql-net                   │
│                                                         │
│  ┌──────────────────┐      ┌──────────────────┐       │
│  │  mysqldb         │      │  security        │       │
│  │  Container       │      │  Container       │       │
│  │                  │      │                  │       │
│  │  MySQL Server    │◄─────┤  Spring Boot     │       │
│  │  Port: 3306      │      │  Port: 8080      │       │
│  │  (Internal only) │      │                  │       │
│  └──────────────────┘      └──────┬───────────┘       │
│                                    │                   │
└────────────────────────────────────┼───────────────────┘
                                     │
                                     │ Port Mapping
                                     │ 8080:8080
                                     ▼
                            ┌────────────────┐
                            │  Host Machine  │
                            │  localhost:8080│
                            └────────────────┘
```

### How Spring Boot Finds MySQL

**Configuration in `application-docker.properties`:**

```properties
spring.datasource.url=jdbc:mysql://mysqldb:3306/ali-security
```

**Key Concept:** `mysqldb` is the **hostname**

- Docker provides DNS resolution within the network
- Service name `mysqldb` resolves to MySQL container's IP address
- No need for IP addresses or localhost
- Automatic service discovery

**Inside the Docker network:**
```
Spring Boot container → DNS lookup for "mysqldb" → MySQL container IP
```

---

## Configuration Breakdown

### 1. `depends_on`

```yaml
depends_on:
  - mysqldb
```

**Purpose:** Control startup order

**What it does:**
- Ensures MySQL container starts before Spring Boot container
- Does NOT wait for MySQL to be fully ready (just started)

**Limitation:**
```
MySQL container starts (0.5 seconds) ✅
Spring Boot starts immediately (1 second) ✅
MySQL still initializing... (5 more seconds)
Spring Boot tries to connect → FAILS! ❌
```

**Solution:** Use `restart: on-failure`

---

### 2. `restart: on-failure`

```yaml
restart: on-failure
```

**Purpose:** Handle MySQL initialization delay

**How it works:**
```
Attempt 1: Spring Boot starts → MySQL not ready → Connection fails → Container exits
           Docker restarts container automatically
Attempt 2: Spring Boot starts → MySQL ready → Connection succeeds ✅
```

**Alternative:** Use health checks

```yaml
mysqldb:
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
    interval: 10s
    timeout: 5s
    retries: 5

security:
  depends_on:
    mysqldb:
      condition: service_healthy
```

---

### 3. `ports`

```yaml
ports:
  - "8080:8080"
```

**Format:** `HOST_PORT:CONTAINER_PORT`

**What it does:**
- Maps container's port 8080 to host machine's port 8080
- Makes application accessible from outside Docker
- Access via: `http://localhost:8080`

**Without port mapping:**
- Application only accessible within Docker network
- Cannot reach from browser/Postman on host machine

**Different port example:**
```yaml
ports:
  - "9090:8080"  # Access via localhost:9090
```

---

### 4. `environment`

```yaml
environment:
  SPRING_PROFILES_ACTIVE: docker
```

**Purpose:** Set Spring profile for container environment

**What it does:**
- Sets environment variable inside container
- Spring Boot reads it at startup
- Loads corresponding properties file

**Your profiles:**
```
application-local.properties    → Local development
application-docker.properties   → Docker Compose
application-test.properties     → GitHub Actions tests
```

---

### 5. `networks`

```yaml
networks:
  springboot-mysql-net:
```

**Purpose:** Create isolated network for services

**Benefits:**
- ✅ Service-name DNS resolution (use `mysqldb` instead of IP)
- ✅ Network isolation (separate from other Docker containers)
- ✅ Secure communication between containers
- ✅ Automatic service discovery

**Without custom network:**
- Containers use default bridge network
- Need IP addresses for communication
- Less isolated, less secure

---

### 6. `build`

```yaml
build:
  context: ./
  dockerfile: Dockerfile
```

**Purpose:** Build custom image from Dockerfile

**Parameters:**
- `context`: Directory containing files to copy (project root)
- `dockerfile`: Name of Dockerfile to use

**Alternative:** Use pre-built image

```yaml
# Instead of building
security:
  image: yourusername/security-project:latest
```

---

## Common Commands

### Starting Services

```bash
# Start in foreground (see logs in terminal)
docker-compose up

# Start in background (detached mode)
docker-compose up -d

# Rebuild images before starting
docker-compose up --build

# Start specific service only
docker-compose up mysqldb
```

### Stopping Services

```bash
# Stop containers (keeps containers and data)
docker-compose stop

# Stop and remove containers (data in volumes persists)
docker-compose down

# Stop, remove containers AND volumes (deletes database!)
docker-compose down -v

# Force remove everything
docker-compose down -v --remove-orphans
```

### Viewing Status and Logs

```bash
# List running containers
docker-compose ps

# View all logs
docker-compose logs

# Follow logs (live updates)
docker-compose logs -f

# Logs for specific service
docker-compose logs -f security
docker-compose logs -f mysqldb

# Last 100 lines
docker-compose logs --tail=100 security
```

### Rebuilding

```bash
# Rebuild specific service
docker-compose build security

# Rebuild all services
docker-compose build

# Rebuild and restart
docker-compose up -d --build

# Force recreate containers (without rebuilding)
docker-compose up -d --force-recreate
```

### Executing Commands Inside Containers

```bash
# Open bash shell in Spring Boot container
docker-compose exec security bash

# Open MySQL shell
docker-compose exec mysqldb mysql -u root -proot ali-security

# Run SQL query directly
docker-compose exec mysqldb mysql -u root -proot -e "SHOW DATABASES;"

# Check network connectivity
docker-compose exec security ping mysqldb

# View environment variables
docker-compose exec security env

# Check if Java is running
docker-compose exec security ps aux
```

### Useful Docker Commands

```bash
# View all containers (including stopped)
docker ps -a

# View images
docker images

# Remove unused images
docker image prune

# Remove all stopped containers
docker container prune

# View networks
docker network ls

# Inspect network
docker network inspect springboot-mysql-net

# View volumes
docker volume ls

# Remove unused volumes
docker volume prune
```

---

## Troubleshooting

### Issue 1: Spring Boot Can't Connect to MySQL

**Symptom:**
```
java.sql.SQLNonTransientConnectionException: 
Could not create connection to database server.
Connection refused: mysqldb:3306
```

**Causes:**
- MySQL not fully initialized when Spring Boot starts
- Network configuration issue
- Wrong credentials

**Solutions:**

1. **Verify `restart: on-failure` is set**
```yaml
security:
  restart: on-failure
```

2. **Add MySQL health check**
```yaml
mysqldb:
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
    interval: 10s
    timeout: 5s
    retries: 5
```

3. **Check logs**
```bash
docker-compose logs mysqldb
docker-compose logs security
```

4. **Verify network connectivity**
```bash
docker-compose exec security ping mysqldb
```

---

### Issue 2: Port Already in Use

**Symptom:**
```
Error starting userland proxy: 
Bind for 0.0.0.0:8080 failed: port is already allocated
```

**Cause:** Another application using port 8080 on host

**Solutions:**

1. **Find and stop the process**
```bash
# Linux/Mac
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

2. **Use different host port**
```yaml
ports:
  - "8081:8080"  # Access via localhost:8081
```

3. **Stop conflicting Docker containers**
```bash
docker ps
docker stop <container_id>
```

---

### Issue 3: Changes Not Reflected

**Symptom:** Code changes not showing in running application

**Cause:** Docker using cached/old image

**Solutions:**

1. **Rebuild the image**
```bash
docker-compose up --build
```

2. **Force recreate containers**
```bash
docker-compose up -d --force-recreate
```

3. **Complete clean rebuild**
```bash
docker-compose down
mvn clean package -DskipTests
docker-compose build --no-cache
docker-compose up -d
```

---

### Issue 4: JAR File Not Found

**Symptom:**
```
COPY failed: stat /var/lib/docker/.../target/security-0.0.1-SNAPSHOT.jar: 
no such file or directory
```

**Cause:** JAR file not built before running docker-compose

**Solution:**

```bash
# Build JAR first
mvn clean package -DskipTests

# Verify JAR exists
ls -la target/

# Then run docker-compose
docker-compose up --build
```

---

### Issue 5: MySQL Data Persists After `down`

**Symptom:** Old data remains after restart

**Cause:** Docker volumes persist data

**Solutions:**

1. **Remove volumes when stopping**
```bash
docker-compose down -v
```

2. **Manually remove volume**
```bash
docker volume ls
docker volume rm <volume_name>
```

3. **Add named volume in docker-compose.yml**
```yaml
mysqldb:
  volumes:
    - mysql-data:/var/lib/mysql

volumes:
  mysql-data:
```

Then remove with:
```bash
docker-compose down -v
```

---

### Issue 6: Container Exits Immediately

**Symptom:** Container starts but exits immediately

**Cause:** Application crashes on startup

**Solutions:**

1. **Check logs**
```bash
docker-compose logs security
```

2. **Run container interactively**
```bash
docker-compose run security bash
java -jar /app/security.jar
```

3. **Check application properties**
```bash
docker-compose exec security cat /app/application-docker.properties
```

4. **Verify environment variables**
```bash
docker-compose exec security env | grep SPRING
```

---

### Issue 7: Database Connection Pool Exhausted

**Symptom:**
```
HikariPool - Connection is not available, request timed out after 30000ms
```

**Causes:**
- Too many concurrent requests
- Connections not being closed properly
- MySQL max connections limit

**Solutions:**

1. **Increase connection pool size**
```properties
# application-docker.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

2. **Increase MySQL max connections**
```yaml
mysqldb:
  command: --max_connections=200
```

3. **Add connection timeout**
```properties
spring.datasource.hikari.connection-timeout=20000
```

---

## Workflow Guide

### Development Workflow

```bash
# 1. Make code changes in your IDE
vim src/main/java/com/alibou/security/...

# 2. Build JAR file
mvn clean package -DskipTests

# 3. Rebuild and restart Docker containers
docker-compose up --build -d

# 4. Check logs to ensure successful startup
docker-compose logs -f security

# 5. Test your application
curl http://localhost:8080/api/auth/register

# 6. View MySQL data (if needed)
docker-compose exec mysqldb mysql -u root -proot ali-security
```

### Quick Restart After Code Changes

```bash
# Stop only Spring Boot container
docker-compose stop security

# Rebuild and start
mvn clean package -DskipTests && docker-compose up --build -d security

# Follow logs
docker-compose logs -f security
```

### Database Operations

```bash
# Access MySQL shell
docker-compose exec mysqldb mysql -u root -proot ali-security

# Export database
docker-compose exec mysqldb mysqldump -u root -proot ali-security > backup.sql

# Import database
docker-compose exec -T mysqldb mysql -u root -proot ali-security < backup.sql

# Reset database (careful!)
docker-compose down -v
docker-compose up -d
```

### Debugging Workflow

```bash
# 1. Check which containers are running
docker-compose ps

# 2. View recent logs
docker-compose logs --tail=50 security

# 3. Follow live logs
docker-compose logs -f

# 4. Check container health
docker inspect security | grep Status

# 5. Access container shell
docker-compose exec security bash

# 6. Test MySQL connection from Spring Boot container
docker-compose exec security ping mysqldb
docker-compose exec security telnet mysqldb 3306

# 7. View application configuration
docker-compose exec security env | grep SPRING
```

---

### Production Deployment Workflow

```bash
# On production server

# 1. Pull latest code from repository
git pull origin main

# 2. Build JAR file
mvn clean package -DskipTests

# 3. Stop current containers gracefully
docker-compose down

# 4. Backup database (important!)
docker-compose up -d mysqldb
sleep 10
docker-compose exec mysqldb mysqldump -u root -proot ali-security > backup_$(date +%Y%m%d).sql
docker-compose down

# 5. Start new version
docker-compose up -d --build

# 6. Monitor startup
docker-compose logs -f

# 7. Verify application is running
curl http://localhost:8080/actuator/health

# 8. Check container status
docker-compose ps
```

### Rollback Workflow

```bash
# 1. Stop current version
docker-compose down

# 2. Checkout previous version
git checkout <previous-commit-hash>

# 3. Rebuild
mvn clean package -DskipTests
docker-compose up -d --build

# 4. Restore database if needed
docker-compose exec -T mysqldb mysql -u root -proot ali-security < backup.sql

# 5. Verify
curl http://localhost:8080/actuator/health
```

---

## Comparison: Local vs Docker Compose

### Running Locally (Docker Desktop MySQL)

```
┌─────────────────────────────────────┐
│ Your Machine                        │
│                                     │
│  ┌──────────────────┐               │
│  │ Docker Desktop   │               │
│  │                  │               │
│  │  ┌───────────┐   │               │
│  │  │  MySQL    │   │               │
│  │  │  :3306    │   │               │
│  │  └─────▲─────┘   │               │
│  └────────┼─────────┘               │
│           │                         │
│           │ localhost:3306          │
│           │                         │
│  ┌────────┴─────────┐               │
│  │  Spring Boot     │               │
│  │  (IntelliJ/CLI)  │               │
│  │  Port 8080       │               │
│  └──────────────────┘               │
│                                     │
│  Profile: local                     │
│  Config: application-local.properties│
└─────────────────────────────────────┘
```

**Characteristics:**
- Spring Boot runs directly on your machine
- MySQL runs in Docker Desktop
- Connection: `jdbc:mysql://localhost:3306/ali-security`
- Profile: `local`
- Development friendly (hot reload, debugging)

---

### Running with Docker Compose

```
┌─────────────────────────────────────────────┐
│ Docker Network: springboot-mysql-net        │
│                                             │
│  ┌───────────────┐    ┌──────────────────┐ │
│  │  MySQL        │    │  Spring Boot     │ │
│  │  Container    │◄───┤  Container       │ │
│  │  :3306        │    │  :8080           │ │
│  │  (internal)   │    │                  │ │
│  └───────────────┘    └─────────┬────────┘ │
│                                 │          │
└─────────────────────────────────┼──────────┘
                                  │
                        Port Mapping 8080:8080
                                  │
                        ┌─────────┴────────┐
                        │  Your Machine    │
                        │  localhost:8080  │
                        └──────────────────┘

Profile: docker
Config: application-docker.properties
```

**Characteristics:**
- Both Spring Boot and MySQL run in containers
- Connection: `jdbc:mysql://mysqldb:3306/ali-security`
- Profile: `docker`
- Production-like environment
- Isolated, portable, reproducible

---

### Feature Comparison

| Aspect | Local Development | Docker Compose |
|--------|------------------|----------------|
| **MySQL Host** | `localhost` | `mysqldb` |
| **MySQL Port** | `3306` (exposed) | `3306` (internal only) |
| **Spring Boot** | Runs on JVM directly | Runs in container |
| **Profile** | `local` | `docker` |
| **Hot Reload** | ✅ Yes (Spring DevTools) | ❌ No (need rebuild) |
| **Debugging** | ✅ Easy (attach debugger) | ⚠️ Possible (remote debug) |
| **Isolation** | ❌ Shares host resources | ✅ Fully isolated |
| **Portability** | ❌ Machine-specific | ✅ Runs anywhere |
| **Production Similarity** | ❌ Different setup | ✅ Very similar |
| **Startup Speed** | ⚡ Fast | 🐢 Slower (container overhead) |
| **Resource Usage** | 💻 Lower | 🖥️ Higher |
| **Team Consistency** | ❌ Varies by machine | ✅ Same for everyone |
| **Network Debugging** | ✅ Easy | ⚠️ More complex |
| **Data Persistence** | ✅ MySQL data volume | ⚠️ Optional (volumes) |

---

### When to Use Each

#### Use Local Development When:
- ✅ Actively developing and testing frequently
- ✅ Need hot reload for fast iteration
- ✅ Debugging application code
- ✅ Working on features that don't need exact production setup
- ✅ Want faster startup times

#### Use Docker Compose When:
- ✅ Testing deployment configuration
- ✅ Reproducing production issues
- ✅ Sharing consistent environment with team
- ✅ Testing Docker-specific configurations
- ✅ Preparing for production deployment
- ✅ Need complete isolation
- ✅ Testing with multiple services

---

## Key Takeaways

### What Docker Compose Does in Your Project

1. ✅ **Creates isolated network** (`springboot-mysql-net`) for MySQL and Spring Boot
2. ✅ **Starts MySQL** with your database (`ali-security`) pre-created
3. ✅ **Builds your Spring Boot image** from Dockerfile
4. ✅ **Starts Spring Boot** with `docker` profile activated
5. ✅ **Connects everything automatically** using service names
6. ✅ **Makes app accessible** at `http://localhost:8080`

### Why Use Docker Compose

- ✅ **Reproducible environment** - Same setup on all machines
- ✅ **Production-like** - Tests how it will run in production
- ✅ **Easy deployment** - Single command starts everything
- ✅ **Team collaboration** - Everyone has identical setup
- ✅ **No manual configuration** - Networks and volumes handled automatically
- ✅ **Portable** - Works on any machine with Docker
- ✅ **Isolated** - Doesn't interfere with other applications

### Your Complete Project Flow

```
1. Write Code
   ↓
2. Build JAR: mvn clean package -DskipTests
   ↓
3. Start Services: docker-compose up -d --build
   ↓
4. Application Running at http://localhost:8080
   ↓
5. Make Changes → Repeat from step 2
```

### Essential Commands

```bash
# Start everything
docker-compose up -d --build

# View logs
docker-compose logs -f security

# Stop everything
docker-compose down

# Complete reset (including database)
docker-compose down -v
```

---

## Additional Resources

### Docker Compose Documentation
- [Official Docker Compose Docs](https://docs.docker.com/compose/)
- [Compose File Reference](https://docs.docker.com/compose/compose-file/)
- [Docker Networking](https://docs.docker.com/network/)

### Spring Boot with Docker
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Spring Boot Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)

### Best Practices
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Multi-stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Docker Security](https://docs.docker.com/engine/security/)

---

## Quick Reference Card

### Most Common Commands

| Command | Description |
|---------|-------------|
| `docker-compose up` | Start services in foreground |
| `docker-compose up -d` | Start services in background |
| `docker-compose up --build` | Rebuild and start |
| `docker-compose down` | Stop and remove containers |
| `docker-compose down -v` | Stop, remove containers and volumes |
| `docker-compose ps` | List running containers |
| `docker-compose logs -f` | Follow logs |
| `docker-compose logs -f security` | Follow Spring Boot logs |
| `docker-compose exec security bash` | Open shell in container |
| `docker-compose restart security` | Restart specific service |
| `docker-compose build` | Rebuild all images |

### Troubleshooting Commands

| Command | Purpose |
|---------|---------|
| `docker-compose logs security` | Check Spring Boot logs |
| `docker-compose logs mysqldb` | Check MySQL logs |
| `docker-compose exec security ping mysqldb` | Test connectivity |
| `docker-compose exec mysqldb mysql -u root -proot` | Access MySQL |
| `docker network inspect springboot-mysql-net` | Inspect network |
| `docker-compose config` | Validate compose file |
| `docker system prune` | Clean up unused resources |

---

*Last Updated: October 31, 2025*

**Simple as:** `docker-compose up` 🚀