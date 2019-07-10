const myBody ={
    "aggregateBy": [{
      "dataTypeName": "com.google.step_count.delta",
      "dataSourceId": "derived:com.google.step_count.delta:com.google.android.gms:estimated_steps"
    }],
    "bucketByTime": { "durationMillis": 86400000 },
    "startTimeMillis": 1561228200000,
    "endTimeMillis": 1561652514300
    }

const userAction = async () => {
    const response = await fetch('https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate', {
      method: 'POST',
      body: myBody, 
      headers: {
      'Content-Type': 'application/json',
      'Content-Length': '302',
      'Authorization': 'Bearer ' + ya29.Gls_B-dRmqoMcUs3lmLUBaBloJmecC8FkVTyTV71dc8g3-t3vyMiX01Ex94DOYFOSNJMGUublHwXR5mzPnOf5XjmOptbF41IH5svIX5GQQ_GKaFk4_hFSO9XFJRb,
      }
    });
    const myJson = await response.json();
    console.log(myJson);
  }
  userAction();