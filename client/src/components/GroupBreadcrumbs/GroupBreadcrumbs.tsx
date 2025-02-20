import React from 'react';
import { Breadcrumbs, Anchor } from '@mantine/core';
import { Link, useLocation } from 'react-router-dom';
import { useGroupsContext } from '../../providers/GroupsProvider/hooks';

interface GroupBreadcrumbsProps {
  groupSlug?: string;
}

export function GroupBreadcrumbs({ groupSlug }: GroupBreadcrumbsProps) {
  const location = useLocation();
  const { groups } = useGroupsContext();

  const group = groups.find(g => g.slug === groupSlug);
  const path = location.pathname;

  const items = [
    { title: 'Groups', href: '/groups' },
  ];

  if (group) {
    items.push({ title: group.name, href: `/groups/${group.slug}` });

    if (path.includes('/topics')) {
      items.push({ title: 'Topics', href: `/groups/${group.slug}/topics` });
    } else if (path.includes('/theses')) {
      items.push({ title: 'Theses', href: `/groups/${group.slug}/theses` });
    } else if (path.includes('/presentations')) {
      items.push({ title: 'Presentations', href: `/groups/${group.slug}/presentations` });
    } else if (path.includes('/applications')) {
      items.push({ title: 'Applications', href: `/groups/${group.slug}/applications` });
    }
  }

  return (
    <Breadcrumbs>
      {items.map((item, index) => (
        <Anchor
          key={item.href}
          component={Link}
          to={item.href}
          underline={index === items.length - 1 ? 'never' : 'hover'}
        >
          {item.title}
        </Anchor>
      ))}
    </Breadcrumbs>
  );
}