function makeCall(datetime, phone) {
    return $http.post("https://app.jaicp.com/api/calls/campaign/247961767.810664246.eD0ZLrrnbHuPhvi4W0KTSnujT7SYGLkbv2L3AKY47Uu/addPhones", {
        timeout: 10000,
        headers: {"Content-Type": "application/json"},
        body: [{
            "phone": phone,
            "startDateTime": datetime}]
    });
}