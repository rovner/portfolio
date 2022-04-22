#### This is 'portfolio' project with some automation approach examples

##### Requirements
- docker
- docker-compose

##### Start app in dev mode (with hot reload): 
`docker-compose -f docker-compose.dev.yaml build &&  docker-compose -f docker-compose.dev.yaml up`

##### Start app in production mode: 
`docker-compose -f docker-compose.prod.yaml build &&  docker-compose -f docker-compose.prod.yaml up`

See also [backend readme](backend-app/README.md), [frontend readme](frontend-app/README.md), [load tests readme](load-tests/README.md)