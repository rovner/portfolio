FROM node:17.8.0-alpine AS base
WORKDIR /app
COPY package.json .
COPY package-lock.json .
RUN npm install

FROM base AS dev
EXPOSE 3000
CMD ["npm", "start"]

FROM base AS build
COPY . .
RUN npm run build

FROM nginx:1.21.6-alpine as prod
ENV NODE_ENV production
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]