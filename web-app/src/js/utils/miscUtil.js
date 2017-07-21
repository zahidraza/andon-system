export function getMidnightMillis (date) {
  let time = date.getTime();
  let oneDayMillis = 24*60*60*1000;

  let result = time -((time + (5*60 + 30)*60*1000)%oneDayMillis);
  return result;
}
