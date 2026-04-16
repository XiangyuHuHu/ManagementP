import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/Dashboard.vue')
    },
    {
      path: '/monitor',
      name: 'monitor',
      component: () => import('../views/Monitor.vue')
    },
    {
      path: '/alarm',
      name: 'alarm',
      component: () => import('../views/Alarm.vue')
    },
    {
      path: '/device',
      name: 'device',
      component: () => import('../views/Device.vue')
    },
    {
      path: '/report',
      name: 'report',
      component: () => import('../views/Report.vue')
    },
    {
      path: '/workorder',
      name: 'workorder',
      component: () => import('../views/WorkOrder.vue')
    }
  ]
})

export default router
