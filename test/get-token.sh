curl -H "Authorization: Basic Y2xpZW50dGVzdDp0ZXN0Y2xpZW50" \
     -d "grant_type=client_credentials" \
     -d "user=usuario" \
     -d "launch_id=Prueba" \
     -d "role=profesor" \
     http://localhost:8081/demo-oauth-lti/token -o token

 
