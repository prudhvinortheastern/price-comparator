# Deployment Guide - Render

This guide explains how to deploy the Price Comparator application to Render.

## Prerequisites

1. A Render account (sign up at https://render.com)
2. Your GitHub repository pushed to GitHub
3. PostgreSQL database credentials (using Neon or another provider)

## Deployment Steps

### Option 1: Using render.yaml (Recommended)

1. **Connect your GitHub repository:**
   - Go to https://dashboard.render.com
   - Click "New +" and select "Web Service"
   - Connect your GitHub repository

2. **Configure the deployment:**
   - The `render.yaml` file in the root directory will automatically configure your service
   - Render will detect the configuration and use it

3. **Set Environment Variables:**
   - In the Render dashboard, go to your service's "Environment" tab
   - Add the following variables:
     ```
     spring.datasource.url=your_database_url
     spring.datasource.username=your_db_username
     spring.datasource.password=your_db_password
     PORT=8080
     ```

4. **Deploy:**
   - Push your code to GitHub (including render.yaml)
   - Render will automatically deploy on push

### Option 2: Using Render Dashboard (Manual)

1. **Go to Render Dashboard:**
   - Visit https://dashboard.render.com
   - Click "New +" → "Web Service"

2. **Connect GitHub:**
   - Select your price-comparator repository
   - Choose branch to deploy (main/master)

3. **Configure Service:**
   - **Name:** price-comparator
   - **Runtime:** Java
   - **Build Command:** `mvn clean package`
   - **Start Command:** `java -jar target/price-comparator-0.0.1-SNAPSHOT.jar`
   - **Plan:** Free or paid (as needed)

4. **Set Environment Variables:**
   - After service is created, go to "Environment" tab
   - Add all database credentials:
     ```
     spring.datasource.url
     spring.datasource.username
     spring.datasource.password
     ```

5. **Deploy:**
   - Click "Deploy"
   - Monitor the build logs
   - Once successful, your app will be live at: `https://price-comparator.onrender.com`

## Application Configuration

The application is configured to:
- Use port from `PORT` environment variable (defaults to 8080)
- Connect to PostgreSQL database via `spring.datasource.url`
- Auto-update database schema with `ddl-auto=update`

## Important Notes

- **Free Tier Limitations:**
  - Services spin down after 15 minutes of inactivity
  - You can upgrade to a paid plan for always-on service

- **Database URL Format:**
  - If using Neon PostgreSQL: `jdbc:postgresql://host:5432/neondb?sslmode=require`
  - Ensure SSL mode is enabled for security

- **Build Time:**
  - First build may take 3-5 minutes
  - Subsequent builds are usually faster

## Troubleshooting

**Build fails:**
- Check that `pom.xml` is in the root directory
- Verify Java version is 17 or higher
- Check Maven build logs in Render dashboard

**Application crashes after deployment:**
- Check environment variables are set correctly
- Verify database URL and credentials
- Check application logs in Render dashboard

**Database connection errors:**
- Ensure database is accessible from Render
- Add Render IP to database firewall (if applicable)
- Check SSL requirements for your database

## Domain

After successful deployment, your application will be available at:
- `https://price-comparator.onrender.com` (or your custom domain)

You can add a custom domain in the Render dashboard under "Settings" → "Custom Domains".
