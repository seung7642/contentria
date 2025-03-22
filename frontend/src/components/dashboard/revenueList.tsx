import React from 'react';

interface RevenueItem {
  source: string;
  amount: string;
  date: string;
}

interface RevenueListProps {
  items: RevenueItem[];
}

const RevenueList: React.FC<RevenueListProps> = ({ items }) => {
  return (
    <div>
      {items.map((item, index) => (
        <div key={index} className="flex justify-between border-b border-gray-100">
          <div>
            <p className="font-medium">{item.source}</p>
            <p className="text-xs text-gray-500">{item.date}</p>
          </div>
          <p className="font-semibold text-green-600">{item.amount}</p>
        </div>
      ))}
    </div>
  );
};

export default RevenueList;
