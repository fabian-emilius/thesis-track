import { Card, Image, Text, Button, Group }

export default GroupCard from '@mantine/core'
import { Link } from 'react-router-dom'
import { IGroup } from '../../providers/GroupContext/types'
import placeholderImage from '../../assets/placeholder.svg'

/**
 * Props for the GroupCard component
 * @interface GroupCardProps
 * @property {IGroup} group - The group data to display in the card
 */
interface GroupCardProps {
  /** The group data to display */
  group: IGroup
}

/**
 * GroupCard displays a research group's information in a card format
 * with an optional logo, name, description and a link to view more details.
 * 
 * @example
 * ```tsx
 * const group = {
 *   name: "Research Group",
 *   description: "Group description",
 *   slug: "research-group",
 *   logoUrl: "https://example.com/logo.png"
 * };
 * 
 * <GroupCard group={group} />
 * ```
 */
const GroupCard = ({ group }: GroupCardProps): JSX.Element => {
  return (
    <Card shadow="sm" padding="lg" radius="md" withBorder>
      {group.logoUrl && (
        <Card.Section>
          <Image
            src={group.logoUrl}
            height={160}
            alt={group.name}
            fallbackSrc={placeholderImage}
          />
        </Card.Section>
      )}

      <Group justify="space-between" mt="md" mb="xs">
        <Text fw={500} size="lg">{group.name}</Text>
      </Group>

      <Text size="sm" c="dimmed" lineClamp={3}>
        {group.description}
      </Text>

      <Button
        component={Link}
        to={`/groups/${group.slug}`}
        variant="light"
        color="blue"
        fullWidth
        mt="md"
        radius="md"
      >
        View Group
      </Button>
    </Card>
  )
}
