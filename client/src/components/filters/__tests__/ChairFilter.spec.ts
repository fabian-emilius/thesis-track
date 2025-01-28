import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ChairFilter from '../ChairFilter.vue'

describe('ChairFilter', () => {
  const chairs = [
    { id: 1, name: 'Chair A' },
    { id: 2, name: 'Chair B' },
    { id: 3, name: 'Chair C' }
  ]

  it('renders properly', () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: []
      }
    })
    expect(wrapper.text()).toContain('Select Chairs')
  })

  it('shows number of selected chairs', async () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: [chairs[0], chairs[1]]
      }
    })
    expect(wrapper.text()).toContain('2 chair(s) selected')
  })

  it('emits update:modelValue when selection changes', async () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: []
      }
    })

    await wrapper.find('button').trigger('click')
    const options = wrapper.findAll('li')
    await options[0].trigger('click')

    const emitted = wrapper.emitted('update:modelValue')
    expect(emitted).toBeTruthy()
    expect(emitted[0][0]).toEqual([chairs[0]])
  })
})
