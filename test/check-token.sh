TOKEN=$(jq -r '.access_token' token)
curl -i -H "Authorization: Bearer $TOKEN" \
     http://localhost:8081/demo-oauth-lti/access
echo ""
 
