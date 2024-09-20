interface ContentProps {
  children: React.ReactNode;
}

const Content: React.FC<ContentProps> = ({ children }) => {
  return <div className="flex-grow p-4">{children}</div>;
};

export default Content;
