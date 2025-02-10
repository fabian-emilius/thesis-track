FROM node:22-alpine as build

# Set working directory
WORKDIR /app/client

# Copy package files first for better caching
COPY client/package*.json ./

# Install dependencies
ENV CI 1
RUN npm install

# Copy the rest of the client application
COPY client/ ./

# Build the application
RUN npm run build

FROM nginx:stable-alpine

COPY --from=build /app/client/build /usr/share/nginx/html
COPY ./client/nginx/nginx.conf /etc/nginx/conf.d/default.conf

# use same node version like in the build image
COPY --from=build /usr/lib /usr/lib
COPY --from=build /usr/local/share /usr/local/share
COPY --from=build /usr/local/lib /usr/local/lib
COPY --from=build /usr/local/include /usr/local/include
COPY --from=build /usr/local/bin /usr/local/bin

WORKDIR /usr/share/nginx/html

EXPOSE 80

CMD ["/bin/sh", "-c", "node generate-runtime-env.js && nginx -g \"daemon off;\""]