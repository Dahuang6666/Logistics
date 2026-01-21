import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import Register from '@/views/Register.vue'
import StudentDashboard from '@/Student/StudentDashboard.vue'
import AdminDashboard from '@/Admin/AdminDashboard.vue'
import Announcement  from '@/Student/Announcement.vue'
import RepairApply from '@/Student/RepairApply.vue'
import RepairProgress from '@/Student/RepairProgress.vue'
import DormChange from '@/Student/DormChange.vue'
import ChangeProgress from '@/Student/ChangeProgress.vue'
import Feedback from '@/Student/Feedback.vue'
import StudentProfile from '@/Student/StudentProfile.vue'
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
      component: StudentDashboard,
      meta: { requiresAuth: true },
      redirect: '/student/announcement',  // 默认跳转到公告页
      children: [
        {
          path: 'announcement',
          name: 'student-announcement',
          component: Announcement
        },
        {
          path: 'repair',
          name: 'student-repair',
          component: RepairApply
        },
        {
          path: 'repair-progress',
          name: 'student-repair-progress',
          component: RepairProgress
        },
        {
          path: 'dorm-change',
          name: 'student-dorm-change',
          component:DormChange
        },
        {
          path: 'change-progress',
          name: 'student-change-progress',
          component: ChangeProgress
        },
        {
          path: 'feedback',
          name: 'student-feedback',
          component: Feedback
        },
        {
          path: 'profile',
          name: 'student-profile',
          component: StudentProfile
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboard,
      meta: { requiresAuth: true },
    }
  ]
})
// 全局前置守卫 - 修复后的逻辑
router.beforeEach((to, from, next) => {
  // 从 sessionStorage 获取 token
  const isAuthenticated = !!sessionStorage.getItem('token')

  // 核心逻辑：只对需要权限的页面校验登录状态
  if (to.meta.requiresAuth) {
    // 访问需要权限的页面：已登录则放行，未登录则跳登录页
    if (isAuthenticated) {
      next()
    } else {
      next('/') // 未登录跳登录页
    }
  } else {
    next()
  }
})

export default router