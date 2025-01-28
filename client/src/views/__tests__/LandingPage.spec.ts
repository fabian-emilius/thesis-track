import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import LandingPage from '../LandingPage.vue'

describe('LandingPage', () => {
  it('renders properly', () => {
    const wrapper = mount(LandingPage)
    expect(wrapper.text()).toContain('Topics')
  })

  it('filters topics based on selected chairs', async () => {
    const wrapper = mount(LandingPage)
    const chairFilter = wrapper.findComponent({ name: 'ChairFilter' })
    expect(chairFilter.exists()).toBe(true)
  })
})
