export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

export function formatImageUrl(value) {
  if (!value) return ''
  return String(value).trim()
}

export function splitImageUrls(value) {
  if (!value) return []
  return String(value)
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
    .map((url, index) => ({
      id: index,
      url: formatImageUrl(url)
    }))
}
