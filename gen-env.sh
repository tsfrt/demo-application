#!/bin/bash

ENV_NAME=$1
BIN_PATH="/Users/tseufert/sping-k8s/tdm/spring-k8s-demo/build/libs/spring-k8s-demo-0.0.15-SNAPSHOT.jar"
echo "Creating Environment $ENV_NAME>>>>>>>>>>>>>>>."

cf login -s development -u tseufert@pivotal.io -p Elliott0530!

cf create-space $ENV_NAME

cf target -s $ENV_NAME

cf push tdm-$ENV_NAME -p /Users/tseufert/sping-k8s/tdm/spring-k8s-demo/build/libs/spring-k8s-demo-0.0.16-SNAPSHOT.jar
cf push cdm-$ENV_NAME -p /Users/tseufert/sping-k8s/tdm/spring-k8s-demo/build/libs/spring-k8s-demo-0.0.16-SNAPSHOT.jar
cf push tdmc-$ENV_NAME -p /Users/tseufert/sping-k8s/tdm/spring-k8s-demo/build/libs/spring-k8s-demo-0.0.16-SNAPSHOT.jar
cf push portal-$ENV_NAME -p /Users/tseufert/sping-k8s/tdm/spring-k8s-demo/build/libs/spring-k8s-demo-0.0.16-SNAPSHOT.jar
cf push sds-$ENV_NAME -p /Users/tseufert/sping-k8s/tdm/spring-k8s-demo/build/libs/spring-k8s-demo-0.0.16-SNAPSHOT.jar
cf push test-ui-$ENV_NAME -o cloudfoundry/lattice-app --random-route

cf routes
