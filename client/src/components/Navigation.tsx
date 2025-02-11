import React from 'react';
import { Link, useLocation } from 'react-router';
import { Tooltip, Group, Text } from '@mantine/core';
import { NewspaperClipping, Users } from 'phosphor-react';
import * as classes from '../app/layout/AuthenticatedArea/AuthenticatedArea.module.css';

const Navigation = () => {
  const location = useLocation();

  const links = [
    { link: '/dashboard', label: 'Dashboard', icon: NewspaperClipping },
    { link: '/groups', label: 'Groups', icon: Users },
  ];

  return (
    <Group direction="column" spacing="xs">
      {links.map((item) => (
        <Link
          key={item.label}
          to={item.link}
          className={classes.fullLink}
          data-active={location.pathname.startsWith(item.link) || undefined}
        >
          <Tooltip label={item.label} position="right" offset={15}>
            <item.icon className={classes.linkIcon} size={25} />
          </Tooltip>
          <Text>{item.label}</Text>
        </Link>
      ))}
    </Group>
  );
};

export default Navigation;
