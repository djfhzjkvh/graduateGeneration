export function cleanPayload(source, keys) {
  return keys.reduce((payload, key) => {
    payload[key] = source[key] === '' ? null : source[key]
    return payload
  }, {})
}

export function pickPayload(source, keys) {
  return keys.reduce((payload, key) => {
    payload[key] = source[key]
    return payload
  }, {})
}
