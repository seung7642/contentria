import React from 'react';
import Link from 'next/link';

interface QuickAction {
  id: number;
  title: string;
  href: string;
  icon: React.ReactNode;
  color: string;
}

interface QuickActionsProps {
  actions: QuickAction[];
}

const QuickActions: React.FC<QuickActionsProps> = ({ actions }) => {
  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
      {actions.map((action) => (
        <Link
          key={action.id}
          href={action.href}
          className="flex items-center rounded-lg border border-gray-200 p-3 transition hover:bg-gray-50"
        >
          <div className={`mr-3 rounded-full p-2 ${action.color}`}>{action.icon}</div>
          <span>{action.title}</span>
        </Link>
      ))}
    </div>
  );
};

export default QuickActions;
