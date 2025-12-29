import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import Register  from '@/views/Register.vue'
import StudentDashboard from '@/views/StudentDashboard.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/student',
      name: 'student',
      component: StudentDashboard
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboard
    },
  ],
})

export default router
