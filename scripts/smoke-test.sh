#!/bin/sh
# Runs a throw-away curl pod INSIDE the cluster and checks we get an ACK.
set -eu
NAME="smoke-$$"
kubectl run "$NAME" --rm -i --restart=Never --image=curlimages/curl:8.8.0 --command -- \
  sh -c 'curl -sf -X POST http://acknack-service/api/payments -H "Content-Type: application/json" -d "{\"debtorBank\":\"ANZ\",\"creditorBank\":\"NAB\",\"amount\":150.00,\"currency\":\"AUD\"}" | tee /dev/stderr | grep -q "\"status\":\"ACK\""'
echo "Smoke test passed"
