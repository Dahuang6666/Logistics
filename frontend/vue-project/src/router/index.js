import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import Register from '@/views/Register.vue'
import StudentDashboard from '@/views/StudentDashboard.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import Announcement  from '@/views/Announcement.vue'
import DormInfo from '@/views/DormInfo.vue'
import RepairApply from '@/views/RepairApply.vue'
import RepairProgress from '@/views/RepairProgress.vue'
import DormChange from '@/views/DormChange.vue'
import ChangeProgress from '@/views/ChangeProgress.vue'
import Feedback from '@/views/Feedback.vue'
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
          path: 'dorm',
          name: 'student-dorm',
          component: DormInfo
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
// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 判断目标路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  // 从 localStorage 获取 token（或你自己的登录状态）
  const isAuthenticated = !!localStorage.getItem('token')

  if (requiresAuth && !isAuthenticated) {
    // 需要登录但未登录 → 跳转到登录页
    next('/')
  } else if (!requiresAuth && isAuthenticated && to.path === '/') {
    // 已登录用户访问登录页 → 自动跳转到学生主页（可选优化）
    next('/student')
  } else {
    // 其他情况正常放行
    next()
  }
})

export default router