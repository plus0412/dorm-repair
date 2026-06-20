import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

function normalizeUser(payload) {
  if (payload && typeof payload === 'object' && payload.user && typeof payload.user === 'object') {
    return payload.user
  }
  if (payload && typeof payload === 'object') {
    return payload
  }
  return {}
}

export const useUserStore = defineStore('user', () => {
  const token = ref(sessionStorage.getItem(TOKEN_KEY) || '')
  const user = ref(normalizeUser(JSON.parse(sessionStorage.getItem(USER_KEY) || '{}')))
  const displayName = computed(() => user.value.realName || user.value.username || '用户')

  function setToken(newToken) {
    token.value = newToken
    sessionStorage.setItem(TOKEN_KEY, newToken)
  }

  function setUser(newUser) {
    const normalizedUser = normalizeUser(newUser)
    user.value = normalizedUser
    sessionStorage.setItem(USER_KEY, JSON.stringify(normalizedUser))
  }

  function login(userData, userToken) {
    setToken(userToken)
    setUser(userData)
  }

  function logout() {
    token.value = ''
    user.value = {}
    sessionStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(USER_KEY)
  }

  function isLoggedIn() {
    return !!token.value
  }

  return {
    token,
    user,
    displayName,
    setToken,
    setUser,
    login,
    logout,
    isLoggedIn
  }
})
