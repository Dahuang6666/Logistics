import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import Register from '@/views/Register.vue'

// 学生端组件
import StudentDashboard from '@/Student/StudentDashboard.vue'
import Announcement from '@/Student/Announcement.vue'
import RepairApply from '@/Student/RepairApply.vue'
import RepairProgress from '@/Student/RepairProgress.vue'
import DormChange from '@/Student/DormChange.vue'
import ChangeProgress from '@/Student/ChangeProgress.vue'
import Feedback from '@/Student/Feedback.vue'
import StudentProfile from '@/Student/StudentProfile.vue'

// 管理员端组件
import AdminDashboard from '@/Admin/AdminDashboard.vue'
import UserManagement from '@/Admin/UserManagement.vue'
import AnnouncementManagement from '@/Admin/AnnouncementManagement.vue'
import DormitoryManagement from '@/Admin/DormitoryManagement.vue'
import RepairManagement from '@/Admin/RepairManagement.vue'
import DormChangeManagement from '@/Admin/DormChangeManagement.vue'
import FeedbackProcessing from '@/Admin/FeedbackProcessing.vue'
import DataStatistics from '@/Admin/DataStatistics.vue'

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
      redirect: '/student/announcement',
      children: [
        { path: 'announcement', name: 'student-announcement', component: Announcement },
        { path: 'repair', name: 'student-repair', component: RepairApply },
        { path: 'repair-progress', name: 'student-repair-progress', component: RepairProgress },
        { path: 'dorm-change', name: 'student-dorm-change', component: DormChange },
        { path: 'change-progress', name: 'student-change-progress', component: ChangeProgress },
        { path: 'feedback', name: 'student-feedback', component: Feedback },
        { path: 'profile', name: 'student-profile', component: StudentProfile }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboard,
      meta: { requiresAuth: true },
      redirect: '/admin/user', // 管理员登录后默认跳到用户管理
      children: [
        { path: 'user', name: 'admin-user', component: UserManagement },
        { path: 'announcement', name: 'admin-announcement', component: AnnouncementManagement },
        { path: 'dormitory', name: 'admin-dormitory', component: DormitoryManagement },
        { path: 'repair', name: 'admin-repair', component: RepairManagement },
        { path: 'dorm-change', name: 'admin-dorm-change', component: DormChangeManagement },
        { path: 'feedback', name: 'admin-feedback', component: FeedbackProcessing },
        { path: 'statistics', name: 'admin-statistics', component: DataStatistics }
      ]
    }
  ]
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!sessionStorage.getItem('token')
  if (to.meta.requiresAuth) {
    if (isAuthenticated) {
      next()
    } else {
      next('/')
    }
  } else {
    next()
  }
})

export default router