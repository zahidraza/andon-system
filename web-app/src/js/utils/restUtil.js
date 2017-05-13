export function getHeaders () {
  return {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + sessionStorage.access_token
  };
}
