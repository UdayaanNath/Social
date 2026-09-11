from locust import HttpUser, task, between
import random

class APIUser(HttpUser):
    # Simulates think time between requests (1 to 3 seconds)
    wait_time = between(1, 3)

    def on_start(self):
        # Set default headers for all subsequent requests by this user
        self.client.headers.update({
            "Content-Type": "application/json",
            "Accept": "*/*"
        })

    @task(3)
    def test_login(self):
        custom_headers = {"X-API-Key": "test-uuid-"+str(random.random())}
        # Simulates an authentication request routed to the Identity Service
        payload = {"username": "admin", "password": "ChangeMe123!"}
        with self.client.post("/identity/auth/login", json=payload, headers=custom_headers, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                print(f"Login failed: {response.status_code} - {response.text}")
                response.failure(f"Failed with status: {response.status_code}")

    @task(1)
    def test_health(self):
        custom_headers = {"X-API-Key": "test-uuid-"+str(random.random())}
        # Simulates a lighter request, like a gateway health check
        with self.client.get("/identity/health", headers=custom_headers, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                print(f"Health check failed: {response.status_code} - {response.text}")
                response.failure(f"Failed with status: {response.status_code}")