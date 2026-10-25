# ACK/NACK Payment Service - Free CI/CD Practice Project

A small, simulated payment ACK/NACK service (Spring Boot 3, Java 17, optional Kafka).
It is a practice project, not real bank code. Everything runs free on your laptop:
Docker + kind (Kubernetes) + Jenkins in a container.

## 1. Install (one time)
- Docker Desktop (free for personal use) - on Windows enable WSL2 and run all commands in a WSL Ubuntu terminal
- Git, JDK 17, Maven (only needed to run locally outside Jenkins)
- kind and kubectl:
  - macOS: `brew install kind kubectl`
  - Linux/WSL: download binaries from kind.sigs.k8s.io and kubernetes.io/docs/tasks/tools

## 2. Run locally first
    mvn clean verify
    java -jar target/acknack-service-1.0.0.jar
    curl -X POST localhost:8080/api/payments -H "Content-Type: application/json" \
      -d '{"debtorBank":"ANZ","creditorBank":"NAB","amount":150.00,"currency":"AUD"}'
Try currency XYZ, amount 2000000, or same bank on both sides to see NACK reasons.

## 3. Create the Kubernetes cluster
    kind create cluster --name demo
    kubectl get nodes

## 4. Start Jenkins (container on the kind network)
    docker build -t jenkins-cicd ./jenkins
    docker run -d --name jenkins --network kind -u root -p 8081:8080 \
      -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkins-cicd
    kind get kubeconfig --internal --name demo > kubeconfig-internal
    docker exec jenkins mkdir -p /var/jenkins_home/.kube
    docker cp kubeconfig-internal jenkins:/var/jenkins_home/.kube/config
    docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
Open http://localhost:8081, paste the password, choose "Install suggested plugins".

## 5. Push to GitHub and create the pipeline job
    git init && git add . && git commit -m "initial"
    # create a free GitHub repo, then:
    git remote add origin https://github.com/<you>/acknack-cicd-demo.git
    git branch -M main && git push -u origin main
In Jenkins: New Item > Pipeline > "Pipeline script from SCM" > Git > your repo URL > branch main > Script Path `Jenkinsfile`.
Under Build Triggers tick "Poll SCM" with `H/2 * * * *` (a local Jenkins cannot receive GitHub webhooks).
Click Build Now.

## 6. Verify the deployment
    kubectl get pods
    kubectl port-forward svc/acknack-service 9090:80
    curl localhost:9090/actuator/health

## 7. Practice scenarios
1. Break a unit test, push, watch the pipeline stop before deploy.
2. Change a NACK rule, push, watch a rolling update (`kubectl get pods -w`).
3. Set a bad image tag in deployment.yaml and watch rollout fail and roll back.
4. Run Kafka: `docker compose up -d`, then
   `APP_KAFKA_ENABLED=true java -jar target/acknack-service-1.0.0.jar`, and consume with
   `docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment-acknack --from-beginning`
5. Compare with the GitHub Actions workflow in `.github/workflows/ci.yml` (runs on every push, free).
6. Stretch: add SonarQube Community (docker image `sonarqube:community`), Helm chart, ArgoCD, Prometheus/Grafana.

## Clean up
    docker rm -f jenkins kafka; kind delete cluster --name demo
