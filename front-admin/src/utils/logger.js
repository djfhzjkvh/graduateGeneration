const ENABLE_DEBUG_LOG = import.meta.env.DEV

export function logBusiness(scope, action, payload = {}) {
  if (!ENABLE_DEBUG_LOG) return
  console.info(`[${scope}] ${action}`, payload)
}

export function warnBusiness(scope, action, error) {
  if (!ENABLE_DEBUG_LOG) return
  console.warn(`[${scope}] ${action}`, error)
}

export function errorBusiness(scope, action, error) {
  console.error(`[${scope}] ${action}`, error)
}
